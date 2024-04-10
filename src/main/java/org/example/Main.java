package org.example;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar dataset-processor.jar <input-file> <output-file>");
            return;
        }

        System.out.println("Dataset processing started...");
        System.out.println("Input file: " + args[0]);
        System.out.println("Output file: " + args[1]);

        DatasetProcessor processor = new DatasetProcessor();
        try {
            processor.process(args[0], args[1]);
        } catch (InterruptedException e) {
            System.out.println("Error processing dataset: " + e.getMessage());
        }

        System.out.println("Dataset processed successfully");
    }
}
