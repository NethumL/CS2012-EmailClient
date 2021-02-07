package com.nethum.emailclient;

import java.time.LocalDate;

class PersonalRecipient extends Recipient implements Greetable {
    private LocalDate birthday;
    private String nickname;

    public PersonalRecipient(String name, String nickname, String email, LocalDate birthday) {
        super(name, email);
        this.nickname = nickname;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return String.format("Personal: %s,%s,%s,%s", getName(), nickname, getEmail(), Greetable.formatDate(birthday));
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
        return "Hugs and love on your birthday, " + this.nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
