package com.team7.rupiapp.service;

public interface NotifierService {
    void sendEmail(String to, String subject, String message);

    void sendWhatsapp(String to, String message);

    void sendVerification(String to, String otp);

    void sendResetPasswordVerification(String to, String otp);

    void sendVerificationLogin(String to, String otp);

    void sendVerificationEmail(String to, String name, String otp);
}
