package com.nethum.emailclient;

abstract class Recipient {
    private static int recipientCount = 0;
    private String name;
    private String email;

    public Recipient(String name, String email) {
        this.name = name;
        this.email = email;
        recipientCount++;
    }

    public static int getRecipientCount() {
        return recipientCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

