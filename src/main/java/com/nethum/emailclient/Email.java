package com.nethum.emailclient;

import java.io.Serializable;

class Email implements Serializable {
    private static final long serialVersionUID = 5576391924029736857L;
    private String recipientEmailAddress;
    private String subject;
    private String content;

    public Email(String recipientEmailAddress, String subject, String content) {
        this.recipientEmailAddress = recipientEmailAddress;
        this.subject = subject;
        this.content = content;
    }

    public String getRecipientEmailAddress() {
        return recipientEmailAddress;
    }

    public void setRecipientEmailAddress(String recipientEmailAddress) {
        this.recipientEmailAddress = recipientEmailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
