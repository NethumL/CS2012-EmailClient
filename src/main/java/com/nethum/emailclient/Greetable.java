package com.nethum.emailclient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

interface Greetable {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd");

    static String formatDate(LocalDate date) {
        // Formats the given LocalDate object into the required format for storage
        return dateTimeFormatter.format(date);
    }

    // Getter and setter for birthday attribute in classes implementing Greetable
    LocalDate getBirthday();

    void setBirthday(LocalDate birthday);

    String generateBirthdayMessage();

    default boolean hasBirthdayOnDate(LocalDate date) {
        LocalDate birthday = getBirthday();
        return date.getMonth() == birthday.getMonth() && date.getDayOfMonth() == birthday.getDayOfMonth();
    }
}
