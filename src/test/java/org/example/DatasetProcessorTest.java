package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DatasetProcessorTest {

    DatasetProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new DatasetProcessor();
    }

    @Test
    void givenTestFile_whenProcess_thenSuccess() {
        assertTrue(Files.exists(Paths.get("input.csv")));

        assertDoesNotThrow(() -> processor.process("input.csv", "output.csv"));

        Path outputPath = Paths.get("output.csv");
        assertTrue(Files.exists(outputPath));

        int lineCount = 0;
        try (BufferedReader br = Files.newBufferedReader(outputPath)) {
            while (br.readLine() != null) {
                lineCount++;
            }
        } catch (Exception e) {
            fail(e);
        }

        // The number of lines in the test file is 1000000 + header
        assertEquals(1000001, lineCount);
    }

    @Test
    void givenTestFile_whenCheckRandomLine_thenSuccess() {
        Path inputPath = Paths.get("input.csv");
        assertTrue(Files.exists(inputPath));
        Path outputPath = Paths.get("output.csv");
        assertTrue(Files.exists(outputPath));

        int randomLine = (int) (Math.random() * 1000000) + 1;

        int value1 = 0;
        double value2 = 0.0;

        try (BufferedReader br = Files.newBufferedReader(inputPath)) {
            for (int i = 0; i < randomLine; i++) {
                br.readLine();
            }
            String line = br.readLine();
            assertNotNull(line);

            String[] fields = line.split(",");
            value1 = Integer.parseInt(fields[0]);
            value2 = Double.parseDouble(fields[1]);
        } catch (Exception e) {
            fail(e);
        }

        assertDoesNotThrow(() -> processor.process("input.csv", "output.csv"));

        try (BufferedReader br = Files.newBufferedReader(outputPath)) {
            for (int i = 0; i < randomLine; i++) {
                br.readLine();
            }
            String line = br.readLine();
            assertNotNull(line);
            String[] fields = line.split(",");
            assertEquals(String.format("%.8f", value1 + value2), fields[0]);
            assertEquals(String.format("%.8f", value1 * value2), fields[1]);
        } catch (Exception e) {
            fail(e);
        }
    }
}