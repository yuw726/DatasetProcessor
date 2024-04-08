package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVInputReader extends InputReader {

    private static final String HEADER = "#key,#value";

    public CSVInputReader(String filename, int chunkSize) {
        super(filename, chunkSize);
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        if (!filename.endsWith(".csv")) {
            throw new IllegalArgumentException("Only .csv files are supported");
        }
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line = br.readLine();
            validateHeader(line);

            while (line != null) {
                int count = 0;
                final List<String> chunk = new ArrayList<>(chunkSize);
                while ((line = br.readLine()) != null) {
                    if (validateInput(line)) {
                        chunk.add(line);
                    }
                    count++;
                    if (count == chunkSize) {
                        break;
                    }
                }
                // Add records to queue, making them available for processing immediately
                queue.add(chunk);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Mark the end of the queue
        queue.add(Collections.emptyList());
    }

    private void validateHeader(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Input file is empty");
        }
        if (!line.equals(HEADER)) {
            throw new IllegalArgumentException("Input file must start with #key,#value");
        }
    }

    private boolean validateInput(String line) {
        final String[] fields = line.split(",");
        if (fields.length != 2) {
            System.out.println("Invalid record: " + line + ". Must be in format: key,value. Skipping");
            return false;
        }
        try {
            Integer.parseInt(fields[0]);
            Double.parseDouble(fields[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid record: " + line + ". Key must be integer and value must be float. Skipping");
            return false;
        }

        return true;
    }
}