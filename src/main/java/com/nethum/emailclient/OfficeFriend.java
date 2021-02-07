package com.nethum.emailclient;

import java.time.LocalDate;

class OfficeFriend extends OfficialRecipient implements Greetable {
    private LocalDate birthday;

    public OfficeFriend(String name, String email, String designation, LocalDate birthday) {
        super(name, email, designation);
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return String.format("Office_friend: %s,%s,%s,%s", getName(), getEmail(), getDesignation(), Greetable.formatDate(birthday));
    }

    @Override
    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String generateBirthdayMessage() {
        return "Wish you a Happy Birthday, " + getName();
    }
}
