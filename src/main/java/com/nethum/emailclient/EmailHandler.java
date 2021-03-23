package com.nethum.emailclient;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmailHandler {
    private String userEmailAddress;
    private Sender sender;
    private ReceiveThread receiveThread;

    public EmailHandler(String emailAddress, String password) {
        this.userEmailAddress = emailAddress;
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, password);
            }
        };
        sender = new Sender(emailAddress, authenticator);

        MyBlockingQueue queue = new MyBlockingQueue(20);

        receiveThread = new ReceiveThread(queue, emailAddress, authenticator);
        receiveThread.attach(new EmailStatPrinter());
        receiveThread.attach(new EmailStatRecorder());

        SaveThread saveThread = new SaveThread(queue);

        // Start threads
        new Thread(receiveThread).start();
        new Thread(saveThread).start();
    }

    public void sendEmail(String recipientEmailAddress, String subject, String content) {
        Email email = new Email(userEmailAddress, recipientEmailAddress, subject, content);
        sendEmail(email);
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
