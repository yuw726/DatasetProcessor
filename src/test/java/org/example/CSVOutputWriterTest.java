package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CSVOutputWriterTest {

    CSVOutputWriter writer;
    String tmpFile = "tmp.csv";

    @BeforeEach
    void setUp() {
        BlockingQueue<Future<List<Double>>> queue = new LinkedBlockingQueue<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        queue.add(executor.submit(() -> Arrays.asList(1.0, 2.0, 3.0)));
        queue.add(executor.submit(() -> Arrays.asList(4.0, 5.0, 6.0)));
        queue.add(executor.submit(() -> Collections.emptyList()));
        queue.add(executor.submit(() -> Collections.emptyList()));
        writer = new CSVOutputWriter(tmpFile, queue);
    }

    @AfterEach
    void tearDown() {
        File file = new File(tmpFile);
        if (!file.delete()) {
            fail("Unable to delete tmp file");
        }
    }

    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new CSVOutputWriter("test.txt", new LinkedBlockingQueue<>()));
        assertDoesNotThrow(() -> new CSVOutputWriter(tmpFile, new LinkedBlockingQueue<>()));
    }

    @Test
    public void givenTestFile_whenProcess_thenSuccess() throws Exception {
        writer.start();
        writer.join();

        List<String> lines = Files.readAllLines(Paths.get(tmpFile));
        assertEquals("#sum,#prod", lines.get(0));
        assertEquals("1.00000000,4.00000000", lines.get(1));
        assertEquals("2.00000000,5.00000000", lines.get(2));
        assertEquals("3.00000000,6.00000000", lines.get(3));
    }
}