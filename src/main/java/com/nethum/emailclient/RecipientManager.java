package com.nethum.emailclient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

class RecipientManager {
    private ArrayList<Greetable> greetableArrayList;
    private HashMap<String, Recipient> recipients;
    private File clientListFile;

    public RecipientManager() {
        this.greetableArrayList = new ArrayList<>();
        this.recipients = new HashMap<>();

        // Make 'sent' and 'received' directories to serialize emails
        IO.createDirectory("sent");
        IO.createDirectory("received");

        clientListFile = new File("clientList.txt");

        loadRecipients();
        generateBirthdayList();
    }

    public static Recipient getRecipientFromString(String recipientText) {
        // Parsing recipient text
        Recipient recipient;
        try {
            int colonIndex = recipientText.indexOf(":");
            String[] strings = recipientText.substring(colonIndex + 2).split(",");
            switch (recipientText.substring(0, colonIndex)) {
                case "Official":
                    recipient = new OfficialRecipient(
                            strings[0].strip(),
                            strings[1].strip(),
                            strings[2].strip());
                    break;
                case "Office_friend":
                    recipient = new OfficeFriend(
                            strings[0].strip(),
                            strings[1].strip(),
                            strings[2].strip(),
                            LocalDate.parse(strings[3].strip(), Greetable.dateTimeFormatter));
                    break;
                case "Personal":
                    recipient = new PersonalRecipient(
                            strings[0].strip(),
                            strings[1].strip(),
                            strings[2].strip(),
                            LocalDate.parse(strings[3].strip(), Greetable.dateTimeFormatter));
                    break;
                default:
                    recipient = null;
                    break;
            }
        } catch (Exception e) {
            recipient = null;
        }
        return recipient;
    }

    private void loadRecipients() {
        recipients.putAll(IO.readRecipientListFromFile(clientListFile));
    }

    private void generateBirthdayList() {
        for (Recipient recipient : recipients.values()) {
            if (recipient instanceof Greetable) {
                greetableArrayList.add((Greetable) recipient);
            }
        }
    }

    public void addRecipient(String recipientText) {
        Recipient recipient = getRecipientFromString(recipientText);
        recipients.put(recipient.getEmail(), recipient);
        if (recipient instanceof Greetable) {
            greetableArrayList.add((Greetable) recipient);
        }
        saveRecipientToFile(recipient);
    }

    public void saveRecipientToFile(Recipient recipient) {
        try {
            IO.writeToFile(recipient.toString(), clientListFile);
            System.out.println("Recipient saved\n");
        } catch (IOException e) {
            System.out.println("Error in saving recipient to clientList.txt");
        }
    }

    public void printBornOnDate(LocalDate date) {
        boolean atLeastOne = false;
        for (Recipient recipient : recipients.values()) {
            if (recipient instanceof Greetable) {
                Greetable greetableRecipient = (Greetable) recipient;
                if (greetableRecipient.hasBirthdayOnDate(date)) {
                    atLeastOne = true;
                    System.out.println(recipient.getName());
                }
            }
        }
        if (!atLeastOne) System.out.println("You have no recipients with their birthday on " + date);
        System.out.println();
    }

    public void printRecipientCount() {
        int recipientCount = Recipient.getRecipientCount();
        if (recipientCount == 0) {
            System.out.println("You have no recipients");
        } else if (recipientCount == 1) {
            System.out.println("You have 1 recipient");
        } else {
            System.out.println("You have " + Recipient.getRecipientCount() + " recipient(s)");
        }
        System.out.println();
    }

    public ArrayList<Greetable> getGreetableArrayList() {
        return greetableArrayList;
    }

    public HashMap<String, Recipient> getRecipients() {
        return recipients;
    }
}

