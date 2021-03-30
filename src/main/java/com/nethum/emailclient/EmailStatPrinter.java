package com.nethum.emailclient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailStatPrinter implements Observer {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd h:m a");

    public void update(int number) {
        String notification = String.format(
                "%d notification(s) were received at %s",
                number,
                LocalDateTime.now().format(dateTimeFormatter)
        );
        File notificationsFile = new File("notifications.txt");
        new Thread(() -> {
            try {
                IO.writeToFile(notification, notificationsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
