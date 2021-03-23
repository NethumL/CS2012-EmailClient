package com.nethum.emailclient;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public final class IO {
    public static void writeToFile(String text, File file) throws IOException {
        synchronized (file.getCanonicalPath().intern()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            if (!newLineExists(file)) {
                writer.newLine();
            }
            writer.write(text);
            writer.newLine();
            writer.close();
        }
    }

    private static boolean newLineExists(File file) {
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

    public static HashMap<String, Recipient> readRecipientListFromFile(File clientListFile) {
        HashMap<String, Recipient> recipients = new HashMap<>();
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

                        Recipient recipient = RecipientManager.getRecipientFromString(line);
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
        return recipients;
    }

    public static void createDirectory(String directoryName) {
        try {
            Files.createDirectories(Paths.get(directoryName));
        } catch (FileAlreadyExistsException ignored) {
            // If directory already exists, ignore
        } catch (IOException e) {
            System.out.format("Error in created '%s' directory\n", directoryName);
            System.exit(1);
        }
    }
}
