package com.artsolo.bookswap.services;

public interface EmailSender {
    void send(String to, String subject, String text);
    void sendEmailConfirmation(String to, String name, String jwtToken);
    void sendResetPasswordConfirmation(String to, String name, String jwtToken);
}
