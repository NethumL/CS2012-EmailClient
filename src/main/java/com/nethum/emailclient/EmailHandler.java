package com.nethum.emailclient;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmailHandler {
    private Sender sender;

    public EmailHandler(String email, String password) {
        sender = new Sender(email, password);
    }

    public void sendEmail(Email email) {
        sender.sendEmail(email.getRecipientEmailAddress(), email.getSubject(), email.getContent());
        EmailIO.serializeSentEmail(email);
    }

    public ArrayList<Email> deserializeSentEmailsOnDate(LocalDate date) {
        return EmailIO.deserializeSentEmailsOnDate(date);
    }
}
