package com.team7.rupiapp.service;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
@Async
@Service
public class NotifierServiceImpl implements NotifierService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final WhatsappClient whatsappClient;

    private String pathLogo = "src/main/resources/templates/images/RupiApp.png";

    @Value("${client.wahub.key}")
    private String waApiKey;

    public NotifierServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine,
            WhatsappClient whatsappClient) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.whatsappClient = whatsappClient;
    }

    private String getFormattedDate() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jakarta"))
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss zzz"));
    }

    @Override
    public void sendEmail(String to, String subject, String message) {
        sendEmail(to, subject, message, null);
    }

    @Override
    public void sendEmail(String to, String subject, String message, Map<String, File> imageFiles) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);

            if (imageFiles != null) {
                imageFiles.forEach((cid, imageFile) -> {
                    try {
                        helper.addInline(cid, imageFile);
                    } catch (MessagingException e) {
                        log.error("Error adding inline image: " + cid, e);
                    }
                });
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
        }
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

    private SendWhatsappMessageData createWhatsappData(String to, String message) {
        SendWhatsappMessageData data = new SendWhatsappMessageData();
        data.setAuthkey(waApiKey);
        data.setTo(to);
        data.setMessage(message);
        return data;
    }

    @Override
    public void sendWhatsapp(String to, String message) {
        SendWhatsappMessageData data = createWhatsappData(to, message);
        sendWhatsappMessage(data);
    }

    @Override
    public void sendVerification(String to, String otp) {
        String message = "*" + otp
                + "* adalah kode verifikasi Anda. Demi keamanan, jangan bagikan kode ini kepada siapapun.";
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
    public void sendVerificationEmail(String to, String name, String otp) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("otp", otp);
        context.setVariable("message", "Berikut adalah kode verifikasi Anda");

        String subject = "Email Verification";
        String body = templateEngine.process("verificationEmail", context);

        Map<String, File> imageFiles = Map.of("logo", new File(pathLogo));

        sendEmail(to, subject, body, imageFiles);
    }

    @Override
    public void sendAlertEmail(String to, String name, String subject, String message) {
        message = message.replace("{time}", getFormattedDate());

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("message", message);

        String body = templateEngine.process("alertEmail", context);

        Map<String, File> imageFiles = Map.of("logo", new File(pathLogo));

        sendEmail(to, subject, body, imageFiles);
    }

    @Override
    public void sendUsernameByEmail(String to, String name, String username) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);
        context.setVariable("message", "Berikut adalah username Anda");

        String subject = "Username Recovery";
        String body = templateEngine.process("usernameEmail", context);

        Map<String, File> imageFiles = Map.of("logo", new File(pathLogo));

        sendEmail(to, subject, body, imageFiles);
    }

    @Override
    public void sendUsernameByPhone(String to, String username) {
        String message = "*[Username Recovery]*\n\nBerikut adalah username Anda: *" + username + "*";
        sendWhatsappMessage(createWhatsappData(to, message));
    }
}
