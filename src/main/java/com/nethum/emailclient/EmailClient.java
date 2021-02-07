package com.nethum.emailclient;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class EmailClient {
    private static DateTimeFormatter emailFileNameFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private Sender sender;
    private RecipientManager recipientManager;

    public EmailClient(String email, String password) {
        this.sender = new Sender(email, password);
        this.recipientManager = new RecipientManager();

        sendBirthdayEmails();
    }

    public void addRecipient(String input) {
        recipientManager.addRecipient(input);
    }

    public void sendBirthdayEmails() {
        // Testing today's serialized emails file
        LocalDate today = LocalDate.now();
        String fileName = String.format("emails/%s.ser", emailFileNameFormatter.format(today));
        File file = new File(fileName);
        if (!file.exists()) {
            // If the serialized emails file doesn't exist for today,
            // then this is the first time that the client has been opened today
            // So, the emails will be sent now
            for (Greetable greetableRecipient : recipientManager.getGreetableArrayList()) {
                if (greetableRecipient.hasBirthdayOnDate(today)) {
                    Recipient recipient = (Recipient) greetableRecipient;
                    System.out.println("Sending birthday wish to " + recipient.getName() + "...");
                    Email birthdayEmail = new Email(
                            recipient.getEmail(),
                            "Birthday wish",
                            greetableRecipient.generateBirthdayMessage());
                    sendEmail(birthdayEmail);
                }
            }
        }
    }

    public void sendEmail(String emailText) {
        // Parse email text and send email
        String[] strings = emailText.split(",", 3);
        Email email = new Email(strings[0], strings[1], strings[2]);
        sendEmail(email);
    }

    public void sendEmail(Email email) {
        sender.sendEmail(email.getRecipientEmailAddress(), email.getSubject(), email.getContent());
        serializeEmail(email);
    }

    private void serializeEmail(Email email) {
        // Get filename for today
        String filename = String.format("emails/%s.ser", emailFileNameFormatter.format(LocalDate.now()));
        File file = new File(filename);
        if (file.exists()) {
            // If the file already exists, then emails have previously been written to it
            // So, no need to write the stream header
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename, true)) {
                // Overriding writeStreamHeader to not write the header
                protected void writeStreamHeader() throws IOException {
                    reset();
                }
            }) {
                outputStream.writeObject(email);
            } catch (IOException e) {
                System.out.println("Error in serializing email");
            }
        } else {
            // If the file doesn't exist, then run as normal
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename, true))) {
                outputStream.writeObject(email);
            } catch (IOException e) {
                System.out.println("Error in serializing email");
            }
        }
    }

    private ArrayList<Email> deserializeEmailsOnDate(LocalDate date) {
        // Get filename for specified date
        String filename = String.format("emails/%s.ser", emailFileNameFormatter.format(date));
        ArrayList<Email> emails = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            while (true) {
                emails.add((Email) inputStream.readObject());
            }
        } catch (EOFException ignored) {
            // Finished deserializing all emails
        } catch (FileNotFoundException ignored) {
            // No emails sent on that date
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error in deserializing emails\n");
            return null;
        }
        return emails;
    }

    public void printBornOnDate(LocalDate date) {
        recipientManager.printBornOnDate(date);
    }

    public void printEmailsOnDate(LocalDate date) {
        ArrayList<Email> emailsOnDate = deserializeEmailsOnDate(date);
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
}
