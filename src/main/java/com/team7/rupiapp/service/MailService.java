package com.team7.rupiapp.service;

public interface MailService {
    void sendEmail(String to, String subject, String message);

    void sendVerificationEmail(String to, String username, String otp);

    void sendResetPasswordEmail(String to, String username, String otp);

    void sendVerificationLogin(String to, String username, String otp);
}
