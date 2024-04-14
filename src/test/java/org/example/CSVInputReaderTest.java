package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CSVInputReaderTest {

    private CSVInputReader reader;
    String tmpFile = "tmp.csv";

    @BeforeEach
    void setUp() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {
            writer.write("#key,#value\n");

            Random rand = new Random();

            for (int i = 0; i < 10; i++) {
                writer.write(i + "," + rand.nextDouble() + "\n");
            }

            reader = new CSVInputReader(tmpFile, 1000);
        } catch (IOException e) {
            fail("Unable to create tmp file");
        }

    }

    @AfterEach
    void tearDown() {
        File file = new File(tmpFile);
        if (!file.delete()) {
            fail("Unable to delete tmp file");
        }
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new CSVInputReader(null, 10));
        assertThrows(IllegalArgumentException.class, () -> new CSVInputReader("", 10));
        assertThrows(IllegalArgumentException.class, () -> new CSVInputReader("test.txt", 10));
        assertDoesNotThrow(() -> new CSVInputReader(tmpFile, 10));
    }

    @Test
    void givenTestFile_whenProcess_thenSuccess() throws InterruptedException {
        reader.start();
        reader.join();

        Queue<List<String>> queue = reader.getInputQueue();

        // All the file entries will be added to one chunk
        // Additionally there will be an empty terminate chunk
        assertEquals(2, queue.size());
        List<String> chunk = queue.poll();
        assertNotNull(chunk);
        assertEquals(10, chunk.size());
        chunk = queue.poll();
        assertNotNull(chunk);
        assertEquals(0, chunk.size());
    }
}