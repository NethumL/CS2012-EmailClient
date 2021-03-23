package com.nethum.emailclient;

public class SaveThread implements Runnable {
    private final MyBlockingQueue queue;

    public SaveThread(MyBlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            Email email = queue.dequeue();
            if (email == null) {
                // A null element means the email client has stopped
                // and there are no more emails to serialize
                return;
            }
            EmailIO.serializeReceivedEmail(email);
        }
    }
}
