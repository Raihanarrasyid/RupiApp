package com.team7.rupiapp.service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.client.WhatsappClient;
import com.team7.rupiapp.client.data.CheckWhatsappNumberData;
import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotUsernameDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenResponseDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationDto;
import com.team7.rupiapp.dto.auth.signup.SetPasswordDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.signup.SignupResponseDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.util.ApiResponseUtil;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;

import com.team7.rupiapp.repository.OtpRepository;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final NotifierService notifierService;
    private final HttpServletRequest request;

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    private final WhatsappClient whatsappClient;
    private final GenerateService generateService;

    public AuthenticationServiceImpl(
            ModelMapper modelMapper,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            NotifierService notifierService,
            HttpServletRequest request,
            UserRepository userRepository,
            OtpRepository otpRepository,
            WhatsappClient whatsappClient,
            GenerateService generateService) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.notifierService = notifierService;
        this.request = request;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.whatsappClient = whatsappClient;
        this.generateService = generateService;
    }

    @Value("${client.wahub.key}")
    private String waApiKey;

    private String getAuthenticationToken() {
        String authorizationHeader = request.getHeader("Authorization");
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : "";
    }

    private String simplifyUserAgent(String userAgent) {
        String platform = null;
        String browser = null;

        if (userAgent.contains("Windows")) {
            platform = "Windows";
        } else if (userAgent.contains("Macintosh")) {
            platform = "Mac";
        } else if (userAgent.contains("X11")) {
            platform = "Unix";
        } else if (userAgent.contains("Android")) {
            platform = "Android";
        } else if (userAgent.contains("iPhone")) {
            platform = "iPhone";
        }

        if (userAgent.contains("Edg/")) {
            browser = "Edge";
        } else if (userAgent.contains("Chrome/")) {
            browser = "Chrome";
        } else if (userAgent.contains("Safari/") && userAgent.contains("Version/")) {
            browser = "Safari";
        } else if (userAgent.contains("Firefox/")) {
            browser = "Firefox";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) {
            browser = "IE";
        }

        if (platform != null && browser != null) {
            return platform + "(" + browser + ")";
        } else {
            return userAgent;
        }
    }

    @Override
    public SignupResponseDto signup(SignupDto signupDto) {
        CheckWhatsappNumberData data = new CheckWhatsappNumberData();
        data.setAuthkey(waApiKey);
        data.setNumber(signupDto.getPhone());

        try {
            whatsappClient.checkNumber(data);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestException("Number is not valid");
            }
        }

        if (signupDto.getPassword() == null || signupDto.getConfirmPassword() == null) {
            String password = generateService.generatePassword(8);

            User user = modelMapper.map(signupDto, User.class);
            user.setPassword(passwordEncoder.encode(password));
            user.setDefaultPassword(true);

            User savedUser = userRepository.save(user);

            SignupResponseDto signupResponseDto = modelMapper.map(savedUser, SignupResponseDto.class);
            signupResponseDto.setPassword(password);

            return signupResponseDto;
        }

        if (!signupDto.getPassword().equals(signupDto.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password must be the same");
        }

        User user = modelMapper.map(signupDto, User.class);
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setDefaultPassword(false);

        User savedUser = userRepository.save(user);

        String otp = generateService.generateOtp(savedUser, OtpType.LOGIN);
        notifierService.sendVerification(savedUser.getPhone(), otp);

        SignupResponseDto signupResponseDto = modelMapper.map(savedUser, SignupResponseDto.class);
        String[] tokens = jwtService.generateToken(savedUser);
        signupResponseDto.setAccessToken(tokens[0]);
        signupResponseDto.setRefreshToken(tokens[1]);
        signupResponseDto.setPassword(null);

        return signupResponseDto;
    }

    @Override
    public SigninResponseDto signin(SigninDto signinDto) {
        User user = userRepository.findByUsername(signinDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        signinDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SigninResponseDto responseSigninDto = modelMapper.map(user, SigninResponseDto.class);
        String[] tokens = jwtService.generateToken(user);
        responseSigninDto.setAccessToken(tokens[0]);
        responseSigninDto.setRefreshToken(tokens[1]);

        Otp existingOtp = otpRepository.findByUserAndType(user, OtpType.LOGIN).orElse(null);
        if (existingOtp != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextAllowedTime = existingOtp.getExpiryDate().minusMinutes(4);

            if (now.isBefore(nextAllowedTime)) {
                return responseSigninDto;
            }
        }

        if (user.isDefaultPassword()) {
            String otp = generateService.generateOtp(user, OtpType.LOGIN);
            notifierService.sendVerification(user.getPhone(), otp);
        } else {
            String otp = generateService.generateOtp(user, OtpType.LOGIN);
            notifierService.sendVerificationLogin(user.getPhone(), otp);
        }

        return responseSigninDto;
    }

    @Override
    public void resendVerification(ResendVerificationDto resendVerificationDto) {
        User user = userRepository.findByUsername(resendVerificationDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        otpRepository.findByUserAndType(user, OtpType.LOGIN).ifPresent(otp -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextAllowedTime = otp.getExpiryDate().minusMinutes(4);

            if (now.isBefore(nextAllowedTime)) {
                long secondsLeft = Duration.between(now, nextAllowedTime).getSeconds();
                String message = secondsLeft > 60
                        ? String.format("OTP already sent, please wait for %d minutes", (secondsLeft / 60))
                        : String.format("OTP already sent, please wait for %d seconds", secondsLeft);
                throw new BadRequestException(message);
            }
        });

        String otp = generateService.generateOtp(user, OtpType.LOGIN);
        notifierService.sendVerification(user.getPhone(), otp);
    }

    @Override
    public String forgotUsername(ForgotUsernameDto forgotUsernameDto) {
        if (forgotUsernameDto.getDestination().contains("@")) {
            User user = userRepository.findByEmail(forgotUsernameDto.getDestination())
                    .orElseThrow(() -> new BadRequestException("Please enter a valid phone number or email address"));

            notifierService.sendUsernameByEmail(user.getEmail(), user.getAlias(), user.getUsername());

            return "Username has been sent to your email";
        } else {
            User user = userRepository.findByPhone(forgotUsernameDto.getDestination())
                    .orElseThrow(() -> new BadRequestException("Please enter a valid phone number or email address"));

            notifierService.sendUsernameByPhone(user.getPhone(), user.getUsername());

            return "Username has been sent to your phone";
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository.findByUsername(forgotPasswordDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        String otp = generateService.generateOtp(user, OtpType.FORGOT_PASSWORD);
        notifierService.sendResetPasswordVerification(user.getPhone(), otp);
    }

    @Override
    public ResponseEntity<Object> verify(Principal principal, String userAgent, VerificationDto verificationDto) {
        switch (verificationDto.getType()) {
            case LOGIN:
                return handleVerifyLogin(principal, userAgent, verificationDto);
            case FORGOT_PASSWORD:
                return handleForgotPassword(verificationDto);
            default:
                throw new BadRequestException("Invalid OTP type");
        }
    }

    public ResponseEntity<Object> handleVerifyLogin(Principal principal, String userAgent,
            VerificationDto verificationDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.LOGIN)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (!passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            throw new BadRequestException("Invalid OTP");
        }

        otpRepository.delete(otp);

        String token = getAuthenticationToken();

        if (!jwtService.isTokenEnabled(token)) {
            jwtService.verifyToken(token);
        }

        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);

            return ApiResponseUtil.success(HttpStatus.OK, "Registration verified");
        }

        String simplifiedUserAgent = simplifyUserAgent(userAgent);
        Map<String, String> details = new HashMap<>();
        details.put("Time", "{time}");
        details.put("Device", simplifiedUserAgent);

        notifierService.sendAlertEmail(user.getEmail(), user.getAlias(), "Login Berhasil",
                "Login berhasil dilakukan. Jika Anda tidak merasa melakukan login ini, segera hubungi kami.", details);

        return ApiResponseUtil.success(HttpStatus.OK, "Login verified");
    }

    private ResponseEntity<Object> handleForgotPassword(VerificationDto verificationDto) {
        User user = userRepository.findByUsername(verificationDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.FORGOT_PASSWORD)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            notifierService.sendAlertEmail(user.getEmail(), user.getAlias(), "Reset Password Berhasil",
                    "Password Anda berhasil diubah per tanggal {time}. Jika Anda tidak merasa melakukan perubahan ini, segera hubungi kami.");

            user.setPassword(passwordEncoder.encode(verificationDto.getPassword()));
            user.setDefaultPassword(false);
            userRepository.save(user);
            otpRepository.delete(otp);

            return ApiResponseUtil.success(HttpStatus.OK, "Password changed");
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();
        String username = jwtService.extractRefreshUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (jwtService.isRefreshTokenValid(refreshToken, user)) {
            if (!passwordEncoder.matches(refreshTokenDto.getPin(), user.getPin())) {
                throw new BadRequestException("Invalid pin");
            }

            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
            String token = jwtService.generateToken(user, refreshToken);
            refreshTokenResponseDto.setAccessToken(token);
            jwtService.verifyToken(token);

            return refreshTokenResponseDto;
        } else {
            throw new UsernameNotFoundException("Invalid refresh token");
        }
    }

    @Override
    public void setPassword(Principal principal, SetPasswordDto setPasswordDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (!user.isDefaultPassword()) {
            throw new BadRequestException("Password already set");
        }

        if (!setPasswordDto.getPassword().equals(setPasswordDto.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password must be the same");
        }

        user.setPassword(passwordEncoder.encode(setPasswordDto.getPassword()));
        user.setDefaultPassword(false);
        userRepository.save(user);
    }

    @Override
    public void setPin(Principal principal, SetPinDto setPinDto) {
        if (!setPinDto.getPin().equals(setPinDto.getConfirmPin())) {
            throw new BadRequestException("Pin and confirm pin must be the same");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (user.getPin() != null) {
            throw new BadRequestException("Pin already set");
        }

        user.setPin(passwordEncoder.encode(setPinDto.getPin()));
        userRepository.save(user);
    }

    @Override
    public void signOut() {
        jwtService.signOut(getAuthenticationToken());
    }
}