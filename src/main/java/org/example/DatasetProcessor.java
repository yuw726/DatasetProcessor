package org.example;

import java.util.*;
import java.util.concurrent.*;

public class DatasetProcessor {
    private final static int THREADPOOLSIZE = 5;
    protected static final int CHUNKSIZE = 1000;

    private enum Operation {
        sumValues {
            @Override
            public double execute(int a, double b) {
                return a + b;
            }
        },
        multiplyValues {
            @Override
            public double execute(int a, double b) {
                return a * b;
            }
        };

        public abstract double execute(int a, double b);
    }

    private record Task(List<String> chunk, Operation operation) implements Callable<List<Double>> {
        @Override
        public List<Double> call() {
            List<Double> list = new ArrayList<>();
            for (String s : chunk) {
                String[] value = s.split(",");
                double res = operation.execute(Integer.parseInt(value[0]), Double.parseDouble(value[1]));
                list.add(res);
            }
            return list;
        }
    }

    public void process(String source, String target) throws InterruptedException {
        final InputReader readerThread = new CSVInputReader(source, CHUNKSIZE);
        readerThread.start();

        final OutputWriter writerThread = new CSVOutputWriter(target);
        writerThread.start();

        // Process the input divided in chunks
        ExecutorService sumExecutor = Executors.newFixedThreadPool(THREADPOOLSIZE);
        ExecutorService multiplyExecutor = Executors.newFixedThreadPool(THREADPOOLSIZE);

        while (true) {
            final List<String> chunk = readerThread.getInputQueue().take();
            if (chunk.isEmpty()) {
                // This is the end of the queue
                break;
            }

            // For each chunk there will be two tasks (add and multiply) in the result queue
            // and their order is guaranteed by using Futures
            writerThread.getOutputQueue().put(sumExecutor.submit(new Task(chunk, Operation.sumValues)));
            writerThread.getOutputQueue().put(multiplyExecutor.submit(new Task(chunk, Operation.multiplyValues)));
        }
        // We add two terminate tasks because the OutputWriter expects the tasks to come in pairs
        final Future<List<Double>> terminateTask =
                sumExecutor.submit(new Task(Collections.emptyList(), Operation.sumValues));
        writerThread.getOutputQueue().put(terminateTask);
        writerThread.getOutputQueue().put(terminateTask);

        // Wait for the threads to exit
        readerThread.join();
        writerThread.join();
        sumExecutor.shutdown();
        multiplyExecutor.shutdown();
        if (!sumExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
            sumExecutor.shutdownNow();
        }
        if (!multiplyExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
            multiplyExecutor.shutdownNow();
        }
    }
}