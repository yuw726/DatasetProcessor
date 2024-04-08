package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CSVOutputWriter extends OutputWriter {
    private static final String HEADER = "#sum,#prod\n";

    public CSVOutputWriter(String filename, BlockingQueue<Future<List<Double>>> result) {
        super(filename, result);
        if (!filename.endsWith(".csv")) {
            throw new IllegalArgumentException("Only .csv files are supported");
        }
    }

    @Override
    public void run() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(target))) {
            bw.write(HEADER);
            while (true) {
                Future<List<Double>> listOfSums = result.take();
                Future<List<Double>> listOfProds = result.take();

                List<Double> value1 = listOfSums.get();
                List<Double> value2 = listOfProds.get();

                if (value1 != null && value2 != null && !value1.isEmpty() && !value2.isEmpty()) {
                    for (int i = 0; i < value1.size(); i++) {
                        bw.write(String.format("%.8f,%.8f\n", value1.get(i), value2.get(i)));
                    }
                }
                else {
                    break;
                }
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}