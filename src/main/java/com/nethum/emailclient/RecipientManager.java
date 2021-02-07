package com.nethum.emailclient;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

class RecipientManager {
    private ArrayList<Greetable> greetableArrayList;
    private HashMap<String, Recipient> recipients;

    public RecipientManager() {
        this.greetableArrayList = new ArrayList<>();
        this.recipients = new HashMap<>();

        // Make 'emails' directory to store serialized emails
        try {
            Files.createDirectories(Paths.get("emails"));
        } catch (FileAlreadyExistsException ignored) {
            // If emails directory already exists, ignore
        } catch (IOException e) {
            System.out.println("Error in creating emails directory");
            System.exit(1);
        }

        loadRecipients();
        generateBirthdayList();
    }

    private void loadRecipients() {
        File clientListFile = new File("clientList.txt");
        try {
            // Creates file if not exists
            if (!clientListFile.createNewFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(clientListFile))) {
                    String line;
                    while (true) {
                        line = reader.readLine();
                        if (line == null) break;  // Stops reading at end of file
                        line = line.strip();
                        if (line.equals("")) continue; // Skips blank lines

                        Recipient recipient = getRecipientFromString(line);
                        if (recipient == null) continue;
                        recipients.put(recipient.getEmail(), recipient);
                    }
                } catch (IOException e) {
                    System.out.println("Error in reading clientList.txt");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in checking clientList.txt");
            System.exit(1);
        }
    }

    private void generateBirthdayList() {
        for (Recipient recipient : recipients.values()) {
            if (recipient instanceof Greetable) {
                greetableArrayList.add((Greetable) recipient);
            }
        }
    }

    public Recipient getRecipientFromString(String recipientText) {
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

    public void addRecipient(String recipientText) {
        Recipient recipient = getRecipientFromString(recipientText);
        recipients.put(recipient.getEmail(), recipient);
        if (recipient instanceof Greetable) {
            greetableArrayList.add((Greetable) recipient);
        }
        saveRecipientToFile(recipient);
    }

    public void saveRecipientToFile(Recipient recipient) {
        File clientListFile = new File("clientList.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(clientListFile, true))) {
            if (!newLineExists(clientListFile)) {
                writer.newLine();
            }
            writer.write(recipient.toString());
            writer.newLine();
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

    private boolean newLineExists(File file) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = randomAccessFile.length() - 1;
            if (fileLength < 0) {
                return true;
            }
            randomAccessFile.seek(fileLength);
            byte readByte = randomAccessFile.readByte();
            if (readByte == 0xA || readByte == 0xD) {
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error in checking " + file.getName());
        }
        return false;
    }
}

