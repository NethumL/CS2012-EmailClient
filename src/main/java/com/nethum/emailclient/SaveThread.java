package com.nethum.emailclient;

public class SaveThread implements Runnable {
    private final MyBlockingQueue queue;
    private volatile boolean isRunning;

    public SaveThread(MyBlockingQueue queue) {
        this.queue = queue;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            Email email = queue.dequeue();
            EmailIO.serializeReceivedEmail(email);
        }
    }

    public void stopSaving() {
        isRunning = false;
    }
}
