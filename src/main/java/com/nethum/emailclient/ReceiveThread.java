package com.nethum.emailclient;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ReceiveThread implements Runnable {
    private final ArrayList<Observer> observers;
    private final MyBlockingQueue queue;
    private final String userEmailAddress;
    private volatile boolean isRunning;
    private Store store;

    public ReceiveThread(MyBlockingQueue queue, String userEmailAddress, Authenticator authenticator) {
        this.queue = queue;
        observers = new ArrayList<>(2);
        isRunning = true;
        this.userEmailAddress = userEmailAddress;

        Properties properties = new Properties();

        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.host", "imap.gmail.com");
        properties.setProperty("mail.imaps.port", "993");

        Session session = Session.getInstance(properties, authenticator);
        try {
            store = session.getStore("imaps");
            store.connect();
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void notifyAllObservers(int number) {
        for (Observer observer : observers) {
            observer.update(number);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Folder inbox = store.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getUnreadMessageCount();
                if (messageCount > 0) {
                    notifyAllObservers(messageCount);

                    Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                    for (int i = 0; i < messageCount; i++) {
                        Message message = messages[i];
                        Email email = new Email(
                                message.getFrom()[0].toString(), userEmailAddress, message.getSubject(), getMessageText(message)
                        );
                        queue.enqueue(email);
                        message.setFlags(new Flags(Flags.Flag.SEEN), true);
                    }
                }
                inbox.close(true);
            } catch (AuthenticationFailedException e) {
                System.out.println("Authentication failed");
                System.exit(1);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        // Enqueue null to indicate that the email client is being closed
        // and that there are no more emails to serialize
        queue.enqueue(null);
        try {
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getMessageText(Message message) {
        String result = "";
        try {
            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = getTextFromMultipart(mimeMultipart);
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getTextFromMultipart(MimeMultipart mimeMultipart) {
        StringBuilder result = new StringBuilder();
        try {
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append("\n").append(bodyPart.getContent().toString());
                } else if (bodyPart.isMimeType("multipart/*")) {
                    result.append(getTextFromMultipart((MimeMultipart) bodyPart.getContent()));
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public void stopReceiving() {
        isRunning = false;
    }
}
