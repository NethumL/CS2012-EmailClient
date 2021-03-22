package com.nethum.emailclient;

import java.time.LocalDate;

public class EmailStatRecorder implements Observer {
    public void update(int number) {
        System.out.format("%d notification(s) were received at %s", number, LocalDate.now().toString());
    }
}
