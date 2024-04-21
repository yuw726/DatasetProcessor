package org.example;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class OutputWriter extends Thread {

    protected final String target;
    protected final BlockingQueue<Future<List<Double>>> result;

    public OutputWriter(String target) {
        this.target = target;
        this.result = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<Future<List<Double>>> getOutputQueue() {
        return result;
    }
}
