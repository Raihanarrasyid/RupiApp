package com.team7.rupiapp.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordRequestDto;
import com.team7.rupiapp.dto.auth.pin.PinDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenResponseDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationEmailDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.signup.SignupResponseDto;
import com.team7.rupiapp.dto.auth.signup.VerificationEmailDto;
import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.repository.OtpRepository;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final MailService mailService;

    public AuthenticationServiceImpl(
            ModelMapper modelMapper,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            OtpRepository otpRepository,
            MailService mailService) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.mailService = mailService;
    }

    private Random random = new Random();

    private Otp createOtp(User user, OtpType type) {
        int code = random.nextInt(90000000) + 10000000;

        Otp otp = new Otp();
        otp.setCode(passwordEncoder.encode(String.valueOf(code)));
        otp.setUser(user);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otp.setType(type);
        otpRepository.save(otp);

        return new Otp(user, String.valueOf(code), otp.getExpiryDate());
    }

    @Override
    public SignupResponseDto signup(SignupDto signupDto) {
        if (!signupDto.getPassword().equals(signupDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password must be the same");
        }

        User user = modelMapper.map(signupDto, User.class);
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));

        User savedUser = userRepository.save(user);

        Otp otp = createOtp(savedUser, OtpType.REGISTRATION);

        mailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getUsername(), otp.getCode());

        SignupResponseDto signupResponseDto = modelMapper.map(savedUser, SignupResponseDto.class);
        String[] tokens = jwtService.generateToken(savedUser);
        signupResponseDto.setAccessToken(tokens[0]);
        signupResponseDto.setRefreshToken(tokens[1]);

        return signupResponseDto;
    }

    @Override
    public SigninResponseDto signin(SigninDto signinDto) {
        String username = signinDto.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        boolean isUsername = true;

        if (!user.isPresent() && username.contains("@")) {
            user = userRepository.findByEmail(username);
            isUsername = false;
        }

        if (user.isPresent()) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.get().getUsername(),
                            signinDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            SigninResponseDto responseSigninDto = modelMapper.map(user.get(), SigninResponseDto.class);
            String[] tokens = jwtService.generateToken(user.get());
            responseSigninDto.setAccessToken(tokens[0]);
            responseSigninDto.setRefreshToken(tokens[1]);

            return responseSigninDto;
        } else {
            String errorMessage;

            if (!isUsername) {
                errorMessage = "Username or password is incorrect.";
            } else {
                errorMessage = "Email or password is incorrect.";
            }
            throw new UsernameNotFoundException(errorMessage);
        }
    }

    @Override
    public void resendVerificationEmail(ResendVerificationEmailDto resendEmailDto) {
        User user = userRepository.findByEmail(resendEmailDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new IllegalArgumentException("User already verified");
        }

        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        Otp otp = createOtp(user, OtpType.REGISTRATION);

        mailService.sendVerificationEmail(user.getEmail(), user.getUsername(), otp.getCode());
    }

    @Override
    public void verifyEmail(String name, VerificationEmailDto verificationEmailDto) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.REGISTRATION);

        if (otp == null) {
            throw new IllegalArgumentException("Invalid OTP");
        } else if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new IllegalArgumentException("OTP expired");
        }

        if (passwordEncoder.matches(verificationEmailDto.getOtp(), otp.getCode())) {
            user.setEnabled(true);
            userRepository.save(user);
            otpRepository.delete(otp);
        } else {
            throw new IllegalArgumentException("Invalid OTP");
        }
    }

    @Override
    public void forgotPasswordRequest(ForgotPasswordRequestDto forgotPasswordDto) {
        String username = forgotPasswordDto.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        boolean isUsername = true;

        if (!user.isPresent() && username.contains("@")) {
            user = userRepository.findByEmail(username);
            isUsername = false;
        }

        if (user.isPresent()) {
            Otp otp = createOtp(user.get(), OtpType.PASSWORD_RESET);

            mailService.sendResetPasswordEmail(user.get().getEmail(), user.get().getUsername(), otp.getCode());
        } else {
            String errorMessage = isUsername ? "Username not found." : "Email not found.";

            throw new UsernameNotFoundException(errorMessage);
        }
    }

    @Override
    public void forgotPassword(String name, ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.PASSWORD_RESET);

        if (otp == null) {
            throw new IllegalArgumentException("Invalid OTP");
        } else if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new IllegalArgumentException("OTP expired");
        }

        if (otp.getCode().equals(passwordEncoder.encode(forgotPasswordDto.getOtp()))) {
            user.setPassword(passwordEncoder.encode(forgotPasswordDto.getPassword()));
            userRepository.save(user);
            otpRepository.delete(otp);
        } else {
            throw new IllegalArgumentException("Invalid OTP");
        }
    }

    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();
        String username = jwtService.extractRefreshUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isRefreshTokenValid(refreshToken, user)) {
            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
            String token = jwtService.generateToken(user, refreshToken);
            refreshTokenResponseDto.setAccessToken(token);

            return refreshTokenResponseDto;
        } else {
            throw new UsernameNotFoundException("Invalid refresh token");
        }
    }

    @Override
    public ResponseEntity<Object> setPin(String name, SetPinDto setPinDto) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPin(passwordEncoder.encode(setPinDto.getPin()));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}