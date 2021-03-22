package com.nethum.emailclient;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

public class ReceiveThread implements Runnable {
    private final Observer[] observers;
    private final MyBlockingQueue queue;
    private final Properties properties;
    private final String email;
    private final String password;
    private volatile boolean isRunning;

    public ReceiveThread(MyBlockingQueue queue, Observer observer1, Observer observer2, String email, String password) {
        this.queue = queue;
        observers = new Observer[2];
        observers[0] = observer1;
        observers[1] = observer2;
        isRunning = true;
        this.email = email;
        this.password = password;

        properties = new Properties();

        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.host", "imap.gmail.com");
        properties.setProperty("mail.imaps.port", "993");
    }

    public void notifyAllObservers(int number) {
        observers[0].update(number);
        observers[1].update(number);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Session session = Session.getDefaultInstance(properties, null);
                Store store = session.getStore("imaps");
                store.connect(email, password);

                Folder inbox = store.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getUnreadMessageCount();
                if (messageCount > 0) {
                    notifyAllObservers(messageCount);

                    Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                    for (int i = 0; i < messageCount; i++) {
                        Message message = messages[i];
                        Email email = new Email(
                                message.getFrom()[0].toString(), message.getSubject(), getMessageText(message)
                        );
                        queue.enqueue(email);
                        message.setFlags(new Flags(Flags.Flag.SEEN), true);
                    }
                }
                inbox.close(true);
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
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
