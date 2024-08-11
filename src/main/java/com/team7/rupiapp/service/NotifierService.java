package com.team7.rupiapp.service;

import java.io.File;
import java.util.Map;

public interface NotifierService {
    void sendEmail(String to, String subject, String message);

    void sendEmail(String to, String subject, String message, Map<String, File> imageFiles);

    void sendWhatsapp(String to, String message);

    void sendVerification(String to, String otp);

    void sendResetPasswordVerification(String to, String otp);

    void sendVerificationLogin(String to, String otp);

    void sendVerificationEmail(String to, String name, String otp);

    void sendAlertEmail(String to, String name, String subject, String message);

    void sendUsernameByEmail(String to, String name, String username);

    void sendUsernameByPhone(String to, String username);
}
