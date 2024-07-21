package com.team7.rupiapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.client.WhatsappClient;
import com.team7.rupiapp.client.data.SendWhatsappMessageData;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotifierServiceImpl implements NotifierService {
    private final JavaMailSender javaMailSender;
    private final WhatsappClient whatsappClient;

    public NotifierServiceImpl(JavaMailSender javaMailSender, WhatsappClient whatsappClient) {
        this.javaMailSender = javaMailSender;
        this.whatsappClient = whatsappClient;
    }

    @Value("${client.wahub.key}")
    private String waApiKey;

    @Value("${client.wahub.number}")
    private String waNumber;

    @Override
    public void sendEmail(String to, String subject, String message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendVerification(String to, String username, String otp) {
        SendWhatsappMessageData data = new SendWhatsappMessageData();
        data.setAuthkey(waApiKey);
        data.setFrom(waNumber);
        data.setTo(to);
        data.setMessage(
                "*" + otp + "* adalah kode verifikasi Anda. Demi keamanan, jangan bagikan kode ini kepada siapapun.");

        try {
            whatsappClient.sendWhatsappMessage(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification message");
        }
    }

    @Override
    public void sendResetPasswordVerification(String to, String username, String otp) {
        SendWhatsappMessageData data = new SendWhatsappMessageData();

        data.setAuthkey(waApiKey);
        data.setFrom(waNumber);
        data.setTo(to);
        data.setMessage(
                "*" + otp + "* adalah kode verifikasi untuk mengganti password Anda. "
                        + "Demi keamanan, jangan bagikan kode ini kepada siapapun. "
                        + "Jika Anda tidak meminta kode ini, abaikan saja pesan ini.");

        try {
            whatsappClient.sendWhatsappMessage(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification message", e);
        }
    }

    @Override
    public void sendVerificationLogin(String to, String username, String otp) {
        SendWhatsappMessageData data = new SendWhatsappMessageData();

        data.setAuthkey(waApiKey);
        data.setFrom(waNumber);
        data.setTo(to);
        data.setMessage(
                "*" + otp
                        + "* adalah kode verifikasi login Anda. Demi keamanan, jangan bagikan kode ini kepada siapapun.");

        try {
            whatsappClient.sendWhatsappMessage(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification message");
        }
    }
}
