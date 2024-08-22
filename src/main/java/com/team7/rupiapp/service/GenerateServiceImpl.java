package com.team7.rupiapp.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.OtpRepository;
import com.team7.rupiapp.util.QrisUtil;
import com.team7.rupiapp.util.QrisUtil.AdditionalDataFieldTemplate;
import com.team7.rupiapp.util.QrisUtil.MerchantAccountInformation;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenerateServiceImpl implements GenerateService {
    private static final String ALGORITHM = "HmacSHA256";
    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;

    private List<String> testerList;

    @Value("${app.otp.code.length}")
    private int otpCodeLength;

    @Value("${app.otp.code.expiration-time}")
    private int otpExpirationTime;

    @Value("${test.tester:}")
    private String tester;

    public GenerateServiceImpl(PasswordEncoder passwordEncoder, OtpRepository otpRepository) {
        this.passwordEncoder = passwordEncoder;
        this.otpRepository = otpRepository;
    }

    @PostConstruct
    private void init() {
        this.testerList = Arrays.asList(tester.split(","));
    }

    private String generateRandomCode() {
        int code = random.nextInt((int) Math.pow(10, otpCodeLength));
        return String.format("%0" + otpCodeLength + "d", code);
    }

    @Transactional
    private synchronized void createOtp(User user, OtpType type, String newValue, String otpCode) {
        otpRepository.findByUserAndType(user, type).ifPresent(otpRepository::delete);

        Otp otp = new Otp();
        otp.setCode(passwordEncoder.encode(otpCode));
        otp.setUser(user);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(otpExpirationTime));
        otp.setType(type);
        otp.setNewValue(newValue);

        otpRepository.save(otp);
    }

    @Override
    public String generateOtp(User user, OtpType type) {
        return generateOtp(user, type, null);
    }

    @Override
    public String generateOtp(User user, OtpType type, String newValue) {
        String otpCode = generateRandomCode();
        if (testerList.contains(user.getUsername())) {
            otpCode = "123456";
        }

        createOtp(user, type, newValue, otpCode);

        return otpCode;
    }

    @Override
    public String generatePassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Override
    public String generateSignature(String secret, String data) {
        try {
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            byte[] codeBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] codeWithSaltBytes = new byte[codeBytes.length + salt.length];
            System.arraycopy(codeBytes, 0, codeWithSaltBytes, 0, codeBytes.length);
            System.arraycopy(salt, 0, codeWithSaltBytes, codeBytes.length, salt.length);

            Mac sha256Hmac = Mac.getInstance(ALGORITHM);
            sha256Hmac.init(new SecretKeySpec(secret.getBytes(), ALGORITHM));
            byte[] hash = sha256Hmac.doFinal(codeWithSaltBytes);

            byte[] hashWithSalt = new byte[hash.length + salt.length];
            System.arraycopy(hash, 0, hashWithSalt, 0, hash.length);
            System.arraycopy(salt, 0, hashWithSalt, hash.length, salt.length);

            return Base64.getEncoder().encodeToString(hashWithSalt);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating signature", e);
            throw new RuntimeException("Error generating signature");
        }
    }

    @Override
    public boolean verifySignature(String secret, String code, String signature) {
        try {
            byte[] hashWithSalt = Base64.getDecoder().decode(signature);

            byte[] hash = new byte[32];
            byte[] salt = new byte[16];
            System.arraycopy(hashWithSalt, 0, hash, 0, 32);
            System.arraycopy(hashWithSalt, 32, salt, 0, 16);

            byte[] codeBytes = code.getBytes(StandardCharsets.UTF_8);
            byte[] codeWithSaltBytes = new byte[codeBytes.length + salt.length];
            System.arraycopy(codeBytes, 0, codeWithSaltBytes, 0, codeBytes.length);
            System.arraycopy(salt, 0, codeWithSaltBytes, codeBytes.length, salt.length);

            Mac sha256Hmac = Mac.getInstance(ALGORITHM);
            sha256Hmac.init(new SecretKeySpec(secret.getBytes(), ALGORITHM));
            byte[] expectedHash = sha256Hmac.doFinal(codeWithSaltBytes);

            for (int i = 0; i < 32; i++) {
                if (hash[i] != expectedHash[i]) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error verifying signature", e);
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Invalid signature", e);
            throw new IllegalArgumentException("Invalid signature");
        }
    }

    @Override
    public BufferedImage generateQRCodeImage(String qrContent, int width, int height) {
        return generateQRCodeImage(qrContent, width, height, null);
    }

    @Override
    public BufferedImage generateQRCodeImage(String qrContent, int width, int height, BufferedImage image) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
                int logoWidth = Math.min(image.getWidth(), width / 5);
                int logoHeight = Math.min(image.getHeight(), height / 5);
                int logoX = (width - logoWidth) / 2;
                int logoY = (height - logoHeight) / 2;

                Graphics2D g = qrImage.createGraphics();
                g.drawImage(image, logoX, logoY, logoWidth, logoHeight, null);
                g.dispose();
            }

            return qrImage;
        } catch (WriterException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Error generating QR code");
        }
    }

    @Override
    public String generateQrisMPM(User user, String transactionId, Integer amount) {
        QrisUtil.MPM qris = new QrisUtil.MPM();
        qris.setPayloadFormatIndicator("01");
        if (amount == null) {
            qris.setPointOfInitiationMethod("11");
        } else {
            qris.setPointOfInitiationMethod("12");
            qris.setTransactionAmount(amount.toString());
        }

        MerchantAccountInformation merchantAccountInformation = new MerchantAccountInformation();
        merchantAccountInformation.setGloballyUniqueIdentifier("ME.RUPIAPP");
        qris.addMerchantAccountInformation("40", merchantAccountInformation);
        qris.setMerchantCategoryCode("0000");
        qris.setTransactionCurrency("360");
        qris.setCountryCode("ID");
        qris.setMerchantName(user.getFullName());
        qris.setMerchantCity("Jakarta");
        qris.setPostalCode("12345");

        AdditionalDataFieldTemplate additionalDataFieldTemplate = new AdditionalDataFieldTemplate();
        additionalDataFieldTemplate.setReferenceLabel("0804DMCT" + transactionId);
        additionalDataFieldTemplate.setTerminalLabel(user.getAccountNumber() + "00" + user.getAccountNumber().length());
        qris.setAdditionalDataFieldTemplate(additionalDataFieldTemplate);

        try {
            return QrisUtil.MPM.encode(qris);
        } catch (Exception e) {
            log.error("Error generating QRIS MPM", e);
            throw new RuntimeException("Error generating QRIS");
        }
    }

    @Override
    public String generateQrisCPM(User user, String transactionId) {
        QrisUtil.CPM qris = new QrisUtil.CPM();
        qris.setDataPayloadFormatIndicator("CPV01");

        QrisUtil.ApplicationTemplate appTemplate = new QrisUtil.ApplicationTemplate();
        appTemplate.getBertlv().setDataApplicationDefinitionFileName("A0000000888888");
        appTemplate.getBertlv().setDataApplicationLabel("QRISCPMZ");
        qris.getApplicationTemplates().add(appTemplate);

        QrisUtil.CommonDataTemplate cdt = new QrisUtil.CommonDataTemplate();
        cdt.getBertlv().setDataApplicationPAN("1234567891011121");
        cdt.getBertlv().setDataCardholderName("RUPIAPP");
        cdt.getBertlv().setDataIssuerURL("RUPIAPP.ME");
        cdt.getBertlv().setDataLanguagePreference("id");

        QrisUtil.CommonDataTransparentTemplate cdtt = new QrisUtil.CommonDataTransparentTemplate();
        cdtt.getBertlv().setDataIssuerApplicationData("08010Z03000000");
        cdtt.getBertlv().setDataTokenRequestorID(user.getId().toString());
        cdtt.getBertlv().setDataTransactionId(transactionId);
        cdt.getCommonDataTransparentTemplates().add(cdtt);

        qris.getCommonDataTemplates().add(cdt);

        try {
            return qris.generatePayload();
        } catch (Exception e) {
            log.error("Error generating QRIS CPM", e);
            throw new RuntimeException("Error generating QRIS");
        }
    }

}
