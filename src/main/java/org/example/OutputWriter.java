package org.example;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public abstract class OutputWriter extends Thread {

    protected final String target;
    protected final BlockingQueue<Future<List<Double>>> result;

    public OutputWriter(String target, BlockingQueue<Future<List<Double>>> result) {
        this.target = target;
        this.result = result;
    }
}
