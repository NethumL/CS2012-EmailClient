package com.nethum.emailclient;

import java.time.LocalDate;
import java.util.ArrayList;

class EmailClient {
    private RecipientManager recipientManager;
    private EmailHandler emailHandler;

    public EmailClient(String email, String password) {
        this.emailHandler = new EmailHandler(email, password);
        this.recipientManager = new RecipientManager();

        sendBirthdayEmails();
    }

    public void addRecipient(String input) {
        recipientManager.addRecipient(input);
    }

    private void sendBirthdayEmails() {
        if (!EmailIO.haveEmailsSentToday()) {
            // If the serialized emails file doesn't exist for today,
            // then this is the first time that the client has been opened today
            // So, the emails will be sent now
            for (Greetable greetableRecipient : recipientManager.getGreetableArrayList()) {
                if (greetableRecipient.hasBirthdayOnDate(LocalDate.now())) {
                    Recipient recipient = (Recipient) greetableRecipient;
                    System.out.println("Sending birthday wish to " + recipient.getName() + "...");
                    emailHandler.sendEmail(
                            recipient.getEmail(), "Birthday wish", greetableRecipient.generateBirthdayMessage()
                    );
                }
            }
        }
    }

    public void sendEmail(String emailText) {
        // Parse email text and send email
        String[] strings = emailText.split(",", 3);
        emailHandler.sendEmail(strings[0], strings[1], strings[2]);
    }

    public void printBornOnDate(LocalDate date) {
        recipientManager.printBornOnDate(date);
    }

    public void printSentEmailsOnDate(LocalDate date) {
        ArrayList<Email> emailsOnDate = emailHandler.deserializeSentEmailsOnDate(date);
        if (emailsOnDate != null) {
            if (emailsOnDate.isEmpty())
                System.out.println("No emails sent on " + date);
            System.out.println();
            for (Email email : emailsOnDate) {
                System.out.println("Subject: " + email.getSubject());
                System.out.println("To: " + email.getRecipientEmailAddress());
                System.out.println();
            }
        }
    }

    public void printRecipientCount() {
        recipientManager.printRecipientCount();
    }

    public void end() {
        emailHandler.end();
    }
}
