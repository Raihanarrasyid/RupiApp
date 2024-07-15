package com.team7.rupiapp.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
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
    private final MailService mailService;
    private final HttpServletRequest request;

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    public AuthenticationServiceImpl(
            ModelMapper modelMapper,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            MailService mailService,
            HttpServletRequest request,
            UserRepository userRepository,
            OtpRepository otpRepository) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.request = request;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
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
            throw new BadRequestException("Password and confirm password must be the same");
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

            otpRepository.findByUserAndType(user.get(), OtpType.LOGIN).ifPresent(otpRepository::delete);
            Otp otp = createOtp(user.get(), OtpType.LOGIN);
            mailService.sendVerificationLogin(user.get().getEmail(), user.get().getUsername(), otp.getCode());

            return responseSigninDto;
        } else {
            String errorMessage;

            if (!isUsername) {
                errorMessage = "Username or password is incorrect.";
            } else {
                errorMessage = "Email or password is incorrect.";
            }
            throw new BadCredentialsException(errorMessage);
        }
    }

    @Override
    public void resendVerificationEmail(ResendVerificationEmailDto resendEmailDto) {
        User user = userRepository.findByEmail(resendEmailDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new BadRequestException("User already verified");
        }

        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        Otp otp = createOtp(user, OtpType.REGISTRATION);

        mailService.sendVerificationEmail(user.getEmail(), user.getUsername(), otp.getCode());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto forgotPasswordDto) {
        String username = forgotPasswordDto.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        boolean isUsername = true;

        if (!user.isPresent() && username.contains("@")) {
            user = userRepository.findByEmail(username);
            isUsername = false;
        }

        if (user.isPresent()) {
            otpRepository.findByUserAndType(user.get(), OtpType.PASSWORD_RESET).ifPresent(otpRepository::delete);

            Otp otp = createOtp(user.get(), OtpType.PASSWORD_RESET);

            mailService.sendResetPasswordEmail(user.get().getEmail(), user.get().getUsername(), otp.getCode());
        } else {
            String errorMessage = isUsername ? "Username not found." : "Email not found.";

            throw new UsernameNotFoundException(errorMessage);
        }
    }

    @Override
    public ResponseEntity<Object> verify(Principal principal, VerificationDto verificationEmailDto) {
        if (verificationEmailDto.getType() == OtpType.REGISTRATION) {
            return verifyEmail(principal, verificationEmailDto);
        } else if (verificationEmailDto.getType() == OtpType.PASSWORD_RESET) {
            return forgotPassword(verificationEmailDto);
        } else if (verificationEmailDto.getType() == OtpType.LOGIN) {
            return verifyLogin(principal, verificationEmailDto);
        }

        throw new BadRequestException("Invalid type");
    }

    private ResponseEntity<Object> verifyEmail(Principal principal, VerificationDto verificationDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.REGISTRATION)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            user.setEnabled(true);
            userRepository.save(user);
            otpRepository.delete(otp);

            return ApiResponseUtil.success(HttpStatus.OK, "Email verified");
        } else {
            throw new BadRequestException("Invalid OTP");
        }
    }

    private ResponseEntity<Object> forgotPassword(VerificationDto verificationDto) {
        User user = userRepository.findByUsername(verificationDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Otp otp = otpRepository.findByUserAndType(user, OtpType.LOGIN)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP expired");
        }

        if (passwordEncoder.matches(verificationDto.getOtp(), otp.getCode())) {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                    ? authorizationHeader.substring(7)
                    : "";

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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isRefreshTokenValid(refreshToken, user)) {
            if (!passwordEncoder.matches(refreshTokenDto.getPin(), user.getPin())) {
                throw new BadRequestException("Invalid pin");
            }

            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
            String token = jwtService.generateToken(user, refreshToken);
            refreshTokenResponseDto.setAccessToken(token);

            return refreshTokenResponseDto;
        } else {
            throw new UsernameNotFoundException("Invalid refresh token");
        }
    }

    @Override
    public void setPin(String name, SetPinDto setPinDto) {
        if (!setPinDto.getPin().equals(setPinDto.getConfirmPin())) {
            throw new BadRequestException("Pin and confirm pin must be the same");
        }

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPin(passwordEncoder.encode(setPinDto.getPin()));
        userRepository.save(user);
    }

    @Override
    public void signOut(Principal principal) {
        String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                    ? authorizationHeader.substring(7)
                    : "";

        jwtService.signOut(token);
    }
}