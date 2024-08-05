package com.team7.rupiapp.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.team7.rupiapp.client.WhatsappClient;
import com.team7.rupiapp.client.data.CheckWhatsappNumberData;
import com.team7.rupiapp.dto.user.UserChangeEmailDto;
import com.team7.rupiapp.dto.user.UserChangePasswordDto;
import com.team7.rupiapp.dto.user.UserChangePhoneDto;
import com.team7.rupiapp.dto.user.UserChangePinDto;
import com.team7.rupiapp.dto.user.UserChangeProfileDto;
import com.team7.rupiapp.dto.user.UserProfileResponseDto;
import com.team7.rupiapp.dto.user.UserSignatureResponseDto;
import com.team7.rupiapp.dto.user.UserVerifyOtpDto;
import com.team7.rupiapp.dto.user.UserVerifyPasswordDto;
import com.team7.rupiapp.dto.user.UserVerifyPinDto;
import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.OtpRepository;
import com.team7.rupiapp.repository.UserRepository;

import feign.FeignException;

@Service
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GenerateService generateService;
    private final NotifierService notifierService;
    private final WhatsappClient whatsappClient;

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    public UserServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, GenerateService generateService,
            NotifierService notifierService, WhatsappClient whatsappClient, UserRepository userRepository,
            OtpRepository otpRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.generateService = generateService;
        this.notifierService = notifierService;
        this.whatsappClient = whatsappClient;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    @Value("${spring.upload.directory}")
    private String uploadDirectory;

    @Value("${client.wahub.key}")
    private String waApiKey;

    @Override
    public UserProfileResponseDto getUserProfile(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        UserProfileResponseDto userProfile = modelMapper.map(user, UserProfileResponseDto.class);
        userProfile.setName(user.getAlias());

        return userProfile;
    }

    @Override
    public void changeProfile(Principal principal, UserChangeProfileDto userChangeProfileDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        user.setAlias(userChangeProfileDto.getName());
        MultipartFile avatar = userChangeProfileDto.getAvatar();
        if (avatar != null) {
            if (!avatar.getContentType().startsWith("image/")) {
                throw new BadRequestException("Only image files are allowed");
            }

            try {
                String filename = UUID.randomUUID().toString() + "."
                        + FilenameUtils.getExtension(avatar.getOriginalFilename());

                Path uploadPath = Paths.get(uploadDirectory);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String previousAvatar = user.getAvatar();
                if (previousAvatar != null && !previousAvatar.equals("default.png")) {
                    Path previousFilePath = uploadPath.resolve(previousAvatar);
                    Files.deleteIfExists(previousFilePath);
                }

                Path filePath = uploadPath.resolve(filename);
                Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("uploads/"+filename);
            } catch (Exception e) {
                throw new BadRequestException("Failed to upload avatar");
            }
        }

        userRepository.save(user);
    }

    @Override
    public void changeEmail(Principal principal, UserChangeEmailDto userChangeEmailDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (userRepository.existsByEmail(userChangeEmailDto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String otp = generateService.generateOtp(user, OtpType.CHANGE_EMAIL, userChangeEmailDto.getEmail());
        notifierService.sendVerificationEmail(user.getEmail(), user.getAlias(), otp);
    }

    @Override
    public void verifyEmail(Principal principal, UserVerifyOtpDto userVerifyOtpDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.CHANGE_EMAIL)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(userVerifyOtpDto.getOtp(), otp.getCode())) {
            user.setEmail(otp.getNewValue());
            userRepository.save(user);
            otpRepository.delete(otp);
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    @Override
    public void changeNumber(Principal principal, UserChangePhoneDto userChangePhoneDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (userRepository.existsByPhone(userChangePhoneDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        String otp = generateService.generateOtp(user, OtpType.CHANGE_PHONE, userChangePhoneDto.getPhone());

        CheckWhatsappNumberData data = new CheckWhatsappNumberData();
        data.setAuthkey(waApiKey);
        data.setNumber(userChangePhoneDto.getPhone());

        try {
            whatsappClient.checkNumber(data);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestException("Number is not valid");
            }
        }

        notifierService.sendVerificationLogin(userChangePhoneDto.getPhone(), otp);
    }

    @Override
    public void verifyNumber(Principal principal, UserVerifyOtpDto userVerifyOtpDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.CHANGE_PHONE)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(userVerifyOtpDto.getOtp(), otp.getCode())) {
            user.setPhone(otp.getNewValue());
            userRepository.save(user);
            otpRepository.delete(otp);
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    @Override
    public UserSignatureResponseDto verifyPassword(Principal principal, UserVerifyPasswordDto userVerifyPasswordDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (passwordEncoder.matches(userVerifyPasswordDto.getPassword(), user.getPassword())) {
            UserSignatureResponseDto signature = new UserSignatureResponseDto();
            signature.setSignature(generateService.generateSignature(user.getId().toString(), user.getPassword()));

            return signature;
        } else {
            throw new BadRequestException("Invalid password");
        }
    }

    @Override
    public void changePassword(Principal principal, String signature, UserChangePasswordDto userChangePasswordDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (!generateService.verifySignature(user.getId().toString(), user.getPassword(), signature)) {
            throw new BadRequestException("Invalid signature");
        }

        if (!userChangePasswordDto.getPassword().equals(userChangePasswordDto.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password must be the same");
        }

        if (passwordEncoder.matches(userChangePasswordDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(userChangePasswordDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserSignatureResponseDto verifyPin(Principal principal, UserVerifyPinDto userVerifyPinDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (passwordEncoder.matches(userVerifyPinDto.getPin(), user.getPin())) {
            UserSignatureResponseDto signature = new UserSignatureResponseDto();
            signature.setSignature(generateService.generateSignature(user.getId().toString(), user.getPin()));

            return signature;
        } else {
            throw new BadRequestException("Invalid pin");
        }
    }

    @Override
    public void changePin(Principal principal, String signature, UserChangePinDto userChangePinDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (!generateService.verifySignature(user.getId().toString(), user.getPin(), signature)) {
            throw new BadRequestException("Invalid signature");
        }

        if (!userChangePinDto.getPin().equals(userChangePinDto.getConfirmPin())) {
            throw new BadRequestException("Pin and confirm pin must be the same");
        }

        if (passwordEncoder.matches(userChangePinDto.getPin(), user.getPin())) {
            throw new BadRequestException("New pin cannot be the same as the old pin");
        }

        user.setPin(passwordEncoder.encode(userChangePinDto.getPin()));
        userRepository.save(user);
    }
}
