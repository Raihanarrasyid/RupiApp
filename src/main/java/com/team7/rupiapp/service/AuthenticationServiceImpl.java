package com.team7.rupiapp.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

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

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
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

    public AuthenticationServiceImpl(
            ModelMapper modelMapper,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            NotifierService notifierService,
            HttpServletRequest request,
            UserRepository userRepository,
            OtpRepository otpRepository) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.notifierService = notifierService;
        this.request = request;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    @Value("${app.otp.code.length}")
    private int otpCodeLength;

    @Value("${app.otp.code.expiration-time}")
    private int otpExpirationTime;

    private Random random = new Random();

    private String getAuthenticationToken() {
        String authorizationHeader = request.getHeader("Authorization");
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : "";
    }

    private Otp createOtp(User user, OtpType type) {
        int code = random.nextInt((int) Math.pow(10, otpCodeLength));
        String otpCode = String.format("%0" + otpCodeLength + "d", code);
    
        Otp otp = new Otp();
        otp.setCode(passwordEncoder.encode(otpCode));
        otp.setUser(user);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(otpExpirationTime));
        otp.setType(type);
        otpRepository.save(otp);
    
        return new Otp(user, otpCode, otp.getExpiryDate());
    }

    private String generateSimplePassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Override
    public SignupResponseDto signup(SignupDto signupDto) {
        if (signupDto.getPassword() == null || signupDto.getConfirmPassword() == null) {
            String password = generateSimplePassword(8);

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

        Otp otp = createOtp(savedUser, OtpType.REGISTRATION);
        notifierService.sendVerification(savedUser.getPhone(), savedUser.getUsername(), otp.getCode());

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

        otpRepository.findByUserAndType(user, OtpType.LOGIN).ifPresent(otpRepository::delete);

        if (user.isDefaultPassword()) {
            otpRepository.findByUserAndType(user, OtpType.REGISTRATION).ifPresent(otpRepository::delete);

            Otp otp = createOtp(user, OtpType.REGISTRATION);
            notifierService.sendVerification(user.getPhone(), user.getUsername(), otp.getCode());
        } else {
            Otp otp = createOtp(user, OtpType.LOGIN);
            notifierService.sendVerificationLogin(user.getPhone(), user.getUsername(),
                    otp.getCode());
        }

        return responseSigninDto;
    }

    @Override
    public void resendVerification(ResendVerificationDto resendVerificationDto) {
        User user = userRepository.findByUsername(resendVerificationDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (user.isVerified()) {
            throw new BadRequestException("User already verified");
        }

        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        Otp otp = createOtp(user, OtpType.REGISTRATION);
        notifierService.sendVerification(user.getPhone(), user.getUsername(), otp.getCode());
    }

    @Override
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository.findByUsername(forgotPasswordDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        otpRepository.findByUserAndType(user, OtpType.PASSWORD_RESET).ifPresent(otpRepository::delete);

        Otp otp = createOtp(user, OtpType.PASSWORD_RESET);
        notifierService.sendResetPasswordVerification(user.getPhone(), user.getUsername(), otp.getCode());
    }

    @Override
    public ResponseEntity<Object> verify(Principal principal, VerificationDto verificationDto) {
        switch (verificationDto.getType()) {
            case REGISTRATION:
                return verifyRegistration(principal, verificationDto);
            case PASSWORD_RESET:
                return forgotPassword(verificationDto);
            case LOGIN:
                return verifyLogin(principal, verificationDto);
            default:
                throw new BadRequestException("Invalid OTP type");
        }
    }

    private ResponseEntity<Object> verifyRegistration(Principal principal, VerificationDto verificationDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.REGISTRATION)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            user.setVerified(true);
            userRepository.save(user);
            otpRepository.delete(otp);

            jwtService.verifyToken(getAuthenticationToken());

            return ApiResponseUtil.success(HttpStatus.OK, "Registration verified");
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    private ResponseEntity<Object> forgotPassword(VerificationDto verificationDto) {
        User user = userRepository.findByUsername(verificationDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            user.setPassword(passwordEncoder.encode(verificationDto.getPassword()));
            userRepository.save(user);
            otpRepository.delete(otp);

            return ApiResponseUtil.success(HttpStatus.OK, "Password changed");
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    private ResponseEntity<Object> verifyLogin(Principal principal, VerificationDto verificationDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.LOGIN)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            String token = getAuthenticationToken();

            if (jwtService.isTokenEnabled(token)) {
                return ApiResponseUtil.success(HttpStatus.OK, "Login verified");
            }

            jwtService.verifyToken(token);

            return ApiResponseUtil.success(HttpStatus.OK, "Login verified");
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