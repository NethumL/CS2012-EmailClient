package com.nethum.emailclient;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Email_Client {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd");

    public static void main(String[] args) {
        // Email needs to passed as the first commandline argument
        String email = args[0];

        // Uses the java.io.Console class to securely get the password from the shell
        String password = getPasswordFromConsole();
        // In IDEs, this might not work, where Java won't be able to get the java.io.Console instance
        // In those cases, hard-code the password instead
        if (password == null) password = "your_password_here";  // If no java.io.Console instance

        EmailClient emailClient = new EmailClient(email, password);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter option type: \n"
                    + "0 - Quit EmailClient\n"
                    + "1 - Adding a new recipient\n"
                    + "2 - Sending an email\n"
                    + "3 - Printing out all the recipients who have birthdays\n"
                    + "4 - Printing out details of all the emails sent\n"
                    + "5 - Printing out the number of recipient objects in the application");

            int option;
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            if (option == 0) {
                // Close email client
                break;
            }

            String input;
            LocalDate date;

            switch (option) {
                case 1:
                    // Add new recipient
                    scanner.nextLine();
                    System.out.println("Enter in the one of the following formats:");
                    System.out.println("Official: <name>,<email>,<designation>");
                    System.out.println("Office_friend: <name>,<email>,<designation>,<birthday(uuuu/MM/dd)>");
                    System.out.println("Personal: <name>,<nickname>,<email>,<birthday(uuuu/MM/dd)>");
                    System.out.println();
                    input = scanner.nextLine().strip();
                    if (input.length() == 0) {
                        System.out.println("Recipient details missing");
                        continue;
                    }
                    emailClient.addRecipient(input);
                    break;
                case 2:
                    // Send email
                    scanner.nextLine();
                    System.out.println("Enter the email in the following format");
                    System.out.println("<email_address>,<subject>,<content>");
                    System.out.println();
                    input = scanner.nextLine().strip();
                    if (input.length() == 0) {
                        System.out.println("Email details missing");
                        continue;
                    }
                    emailClient.sendEmail(input);
                    break;
                case 3:
                    // Print names of recipients who have their birthday on the input day
                    scanner.nextLine();
                    System.out.println("Enter the date in the format: uuuu/MM/dd");
                    System.out.println();
                    input = scanner.nextLine().strip();
                    if (input.length() == 0) {
                        System.out.println("Input date missing");
                        continue;
                    }
                    date = LocalDate.parse(input, dateTimeFormatter);
                    emailClient.printBornOnDate(date);
                    break;
                case 4:
                    // Print emails sent on the input day
                    scanner.nextLine();
                    System.out.println("Enter the date in the format: uuuu/MM/dd");
                    System.out.println();
                    input = scanner.nextLine().strip();
                    if (input.length() == 0) {
                        System.out.println("Input date missing");
                        continue;
                    }
                    date = LocalDate.parse(input, dateTimeFormatter);
                    emailClient.printEmailsOnDate(date);
                    break;
                case 5:
                    // Print number of recipients in email client
                    emailClient.printRecipientCount();
                    break;
            }
        }
    }

    private static String getPasswordFromConsole() {
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            return null;
        }
        char[] passwordArray = console.readPassword("Enter your password: ");
        return new String(passwordArray);
    }
}
