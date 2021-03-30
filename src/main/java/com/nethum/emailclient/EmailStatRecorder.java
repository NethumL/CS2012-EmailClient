package com.nethum.emailclient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailStatRecorder implements Observer {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd h:m a");

    public void update(int number) {
        System.out.format(
                "%d notification(s) were received at %s\n",
                number,
                LocalDateTime.now().format(dateTimeFormatter)
        );
    }
}
