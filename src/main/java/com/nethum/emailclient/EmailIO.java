package com.nethum.emailclient;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EmailIO {
    private static DateTimeFormatter emailFileNameFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    public static boolean haveEmailsSentToday() {
        LocalDate today = LocalDate.now();
        String fileName = String.format("emails/%s.ser", emailFileNameFormatter.format(today));
        File file = new File(fileName);
        return file.exists();
    }

    public static void serializeSentEmail(Email email) {
        String filename = String.format("sent/%s.ser", emailFileNameFormatter.format(LocalDate.now()));
        File file = new File(filename);
        serializeEmailToFile(email, file);
    }

    public static ArrayList<Email> deserializeSentEmailsOnDate(LocalDate date) {
        // Get filename for specified date
        String filename = String.format("sent/%s.ser", emailFileNameFormatter.format(date));
        return deserializeEmailsFromFile(new File(filename));
    }

    public static void serializeReceivedEmail(Email email) {
        String filename = String.format("received/%s.ser", emailFileNameFormatter.format(LocalDate.now()));
        File file = new File(filename);
        serializeEmailToFile(email, file);
    }

    public static ArrayList<Email> deserializeReceivedEmailsOnDate(LocalDate date) {
        // Get filename for specified date
        String filename = String.format("sent/%s.ser", emailFileNameFormatter.format(date));
        return deserializeEmailsFromFile(new File(filename));
    }

    private static void serializeEmailToFile(Email email, File file) {
        if (file.exists()) {
            // If the file already exists, then emails have previously been written to it
            // So, no need to write the stream header
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, true)) {
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
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, true))) {
                outputStream.writeObject(email);
            } catch (IOException e) {
                System.out.println("Error in serializing email");
            }
        }
    }

    private static ArrayList<Email> deserializeEmailsFromFile(File file) {
        ArrayList<Email> emails = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
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
}
