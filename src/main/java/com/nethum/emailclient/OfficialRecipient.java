package com.nethum.emailclient;

class OfficialRecipient extends Recipient {
    private String designation;

    public OfficialRecipient(String name, String email, String designation) {
        super(name, email);
        this.designation = designation;
    }

    @Override
    public String toString() {
        return String.format("Official: %s,%s,%s", getName(), getEmail(), designation);
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
