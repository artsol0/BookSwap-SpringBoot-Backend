package com.artsolo.bookswap.services;

public interface EmailSender {
    void send(String to, String subject, String text);
}
