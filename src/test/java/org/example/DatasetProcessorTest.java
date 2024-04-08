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
}