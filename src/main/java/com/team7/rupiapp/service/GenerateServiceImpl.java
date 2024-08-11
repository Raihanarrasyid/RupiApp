package com.team7.rupiapp.service;

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

import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.OtpRepository;

import jakarta.annotation.PostConstruct;
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

    private Otp createOtp(User user, OtpType type, String newValue, String otpCode) {
        otpRepository.findByUserAndType(user, type).ifPresent(otpRepository::delete);

        Otp otp = new Otp();
        otp.setCode(passwordEncoder.encode(otpCode));
        otp.setUser(user);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(otpExpirationTime));
        otp.setType(type);
        otp.setNewValue(newValue);
        return otp;
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

        Otp otp = createOtp(user, type, newValue, otpCode);
        otpRepository.save(otp);

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
        }
    }
}
