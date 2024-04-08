package org.example;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class InputReader extends Thread {
    protected final BlockingQueue<List<String>> queue;
    protected final String source;
    protected final int chunkSize;

    protected InputReader(String source, int chunkSize) {
        this.queue = new LinkedBlockingQueue<>();
        this.source = source;
        this.chunkSize = chunkSize;
    }

    public BlockingQueue<List<String>> getInputQueue() {
        return queue;
    }
}
