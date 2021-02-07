package com.nethum.emailclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

class Sender {
    private final String emailAddress;

    Properties prop;
    Session session;

    public Sender(String emailAddress, String password) {
        this.emailAddress = emailAddress;

        // Preparing to send emails
        prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(prop, new Authenticator() {
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
