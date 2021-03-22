package com.nethum.emailclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

class Sender {
    private final String emailAddress;

    Properties properties;
    Session session;

    public Sender(String emailAddress, String password) {
        this.emailAddress = emailAddress;

        // Preparing to send emails
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, password);
            }
        });
    }

    public void sendEmail(String recipientEmailAddress, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAddress));
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
            System.out.println("Incorrect password");
            System.exit(1);
        } catch (MessagingException e) {
            System.out.println("Error in sending email");
        }
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
