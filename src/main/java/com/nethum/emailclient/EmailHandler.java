package com.nethum.emailclient;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmailHandler {
    private Sender sender;
    private ReceiveThread receiveThread;

    public EmailHandler(String email, String password) {
        sender = new Sender(email, password);

        MyBlockingQueue queue = new MyBlockingQueue(20);

        receiveThread = new ReceiveThread(queue, new EmailStatPrinter(), new EmailStatRecorder(), email, password);
        SaveThread saveThread = new SaveThread(queue);

        // Start threads
        new Thread(receiveThread).start();
        new Thread(saveThread).start();
    }

    public void sendEmail(Email email) {
        sender.sendEmail(email.getRecipientEmailAddress(), email.getSubject(), email.getContent());
        EmailIO.serializeSentEmail(email);
    }

    public ArrayList<Email> deserializeSentEmailsOnDate(LocalDate date) {
        return EmailIO.deserializeSentEmailsOnDate(date);
    }

    public void end() {
        receiveThread.stopReceiving();
    }
}
