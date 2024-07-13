package com.team7.rupiapp.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.TemplateEngine;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public MailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
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
            e.printStackTrace();
        }
    }

    @Override
    public void sendVerificationEmail(String to, String username, String otp) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("verificationOtp", otp);

        String subject = "Email Verification";
        String body = templateEngine.process("verificationEmail", context);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendResetPasswordEmail(String to, String username, String otp) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("resetPasswordOtp", otp);

        String subject = "Reset Password";
        String body = templateEngine.process("resetPasswordEmail", context);

        sendEmail(to, subject, body);
    }
}
