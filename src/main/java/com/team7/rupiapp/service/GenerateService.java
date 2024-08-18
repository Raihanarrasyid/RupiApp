package com.team7.rupiapp.service;

import java.awt.image.BufferedImage;

import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.model.User;

public interface GenerateService {
    public String generateOtp(User user, OtpType type);

    public String generateOtp(User user, OtpType type, String newValue);

    public String generatePassword(int length);

    public String generateSignature(String secret, String data);

    public boolean verifySignature(String secret, String code, String signature);

    public BufferedImage generateQRCodeImage(String qrContent, int width, int height);

    public BufferedImage generateQRCodeImage(String qrContent, int width, int height, BufferedImage image);

    public String generateQrisMPM(User user, String transactionId, Integer amount);

    public String generateQrisCPM(User user, String transactionId);
}
