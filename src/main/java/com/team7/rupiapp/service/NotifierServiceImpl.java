package com.team7.rupiapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.team7.rupiapp.client.WhatsappClient;
import com.team7.rupiapp.client.data.SendWhatsappMessageData;
import com.team7.rupiapp.exception.BadRequestException;

import feign.FeignException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotifierServiceImpl implements NotifierService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final WhatsappClient whatsappClient;

    @Value("${client.wahub.key}")
    private String waApiKey;

    public NotifierServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine,
            WhatsappClient whatsappClient) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.whatsappClient = whatsappClient;
    }

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
            log.error("Error sending email", e);
        }
    }

    @Override
    public void sendWhatsapp(String to, String message) {
        SendWhatsappMessageData data = createWhatsappData(to, message);
        sendWhatsappMessage(data);
    }

    @Override
    public void sendVerification(String to, String otp) {
        String message = "*" + otp + "* adalah kode verifikasi Anda. Demi keamanan, jangan bagikan kode ini kepada siapapun.";
        sendWhatsappMessage(createWhatsappData(to, message));
    }

    @Override
    public void sendResetPasswordVerification(String to, String otp) {
        String message = "*" + otp + "* adalah kode verifikasi untuk mengganti password Anda. "
                + "Demi keamanan, jangan bagikan kode ini kepada siapapun. "
                + "Jika Anda tidak meminta kode ini, abaikan saja pesan ini.";
        sendWhatsappMessage(createWhatsappData(to, message));
    }

    @Override
    public void sendVerificationLogin(String to, String otp) {
        String message = "*" + otp
                + "* adalah kode verifikasi login Anda. Demi keamanan, jangan bagikan kode ini kepada siapapun.";
        sendWhatsappMessage(createWhatsappData(to, message));
    }

    @Override
    public void sendVerificationEmail(String to, String username, String otp) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("otp", otp);
        context.setVariable("message", "Use the following OTP to verify your email address");

        String subject = "Email Verification";
        String body = templateEngine.process("verificationEmail", context);

        sendEmail(to, subject, body);
    }

    private SendWhatsappMessageData createWhatsappData(String to, String message) {
        SendWhatsappMessageData data = new SendWhatsappMessageData();
        data.setAuthkey(waApiKey);
        data.setTo(to);
        data.setMessage(message);
        return data;
    }

    private void sendWhatsappMessage(SendWhatsappMessageData data) {
        try {
            whatsappClient.sendMessage(data);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestException("Number is not valid");
            }
            log.error("Error sending whatsapp message", e);
        }
    }
}
