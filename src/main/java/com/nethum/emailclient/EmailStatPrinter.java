package com.nethum.emailclient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class EmailStatPrinter implements Observer {
    public void update(int number) {
        String notification = String.format("%d notification(s) were received at %s", number, LocalDate.now().toString());
        File notificationsFile = new File("notifications.txt");
        try {
            IO.writeToFile(notification, notificationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
