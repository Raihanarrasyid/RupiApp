package com.team7.rupiapp.service;

public interface NotifierService {
    void sendEmail(String to, String subject, String message);

    void sendVerification(String to, String username, String otp);

    void sendResetPasswordVerification(String to, String username, String otp);

    void sendVerificationLogin(String to, String username, String otp);
}
