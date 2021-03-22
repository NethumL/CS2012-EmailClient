package com.nethum.emailclient;

import java.util.LinkedList;

public class MyBlockingQueue {
    private final int maxSize;
    private LinkedList<Email> linkedList;

    public MyBlockingQueue(int maxSize) {
        linkedList = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public synchronized void enqueue(Email email) {
        while (linkedList.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        linkedList.add(email);
        notifyAll();
    }

    public synchronized Email dequeue() {
        while (linkedList.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Email email = linkedList.remove();
        notifyAll();
        return email;
    }
}
