package org.example;

import java.util.*;
import java.util.concurrent.*;

public class DatasetProcessor {
    private final BlockingQueue<Future<List<Double>>> result = new LinkedBlockingQueue<>();

    private final static int THREADPOOLSIZE = 5;
    protected static final int CHUNKSIZE = 1000;

    private enum Operation {
        sumValues {
            @Override
            public double execute(double a, double b) {
                return a + b;
            }
        },
        multiplyValues {
            @Override
            public double execute(double a, double b) {
                return a * b;
            }
        };

        public abstract double execute(double a, double b);
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

        final OutputWriter writerThread = new CSVOutputWriter(target, result);
        writerThread.start();

        // Process the input divided in chunks
        try (ExecutorService sumExecutor = Executors.newFixedThreadPool(THREADPOOLSIZE);
             ExecutorService multiplyExecutor = Executors.newFixedThreadPool(THREADPOOLSIZE)) {

            while (true) {
                final List<String> chunk = readerThread.getInputQueue().take();
                if (chunk.isEmpty()) {
                    // This is the end of the queue
                    break;
                }

                // For each chunk there will be two tasks (add and multiply) in the result queue
                // and their order is guaranteed by using Futures
                result.put(sumExecutor.submit(new Task(chunk, Operation.sumValues)));
                result.put(multiplyExecutor.submit(new Task(chunk, Operation.multiplyValues)));
            }
            // We add two terminate tasks because the OutputWriter expects the tasks to come in pairs
            final Future<List<Double>> terminateTask =
                    sumExecutor.submit(new Task(Collections.emptyList(), Operation.sumValues));
            result.put(terminateTask);
            result.put(terminateTask);
        }

        readerThread.join();
        writerThread.join();
    }
}