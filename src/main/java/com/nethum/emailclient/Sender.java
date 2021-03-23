package com.nethum.emailclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

class Sender {
    private final String userEmailAddress;

    Session session;

    public Sender(String userEmailAddress, Authenticator authenticator) {
        this.userEmailAddress = userEmailAddress;

        // Preparing to send emails
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(properties, authenticator);
    }

    public void sendEmail(String recipientEmailAddress, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userEmailAddress));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmailAddress)
            );
            message.setSubject(subject);
            message.setText(body);

            System.out.println("Sending your email");
            Transport.send(message);
            System.out.println("Email sent successfully!\n");
        } catch (AuthenticationFailedException e) {
            System.out.println("Authentication failed");
            System.exit(1);
        } catch (MessagingException e) {
            System.out.println("Error in sending email");
        }
    }

    public String getEmailAddress() {
        return userEmailAddress;
    }
}
