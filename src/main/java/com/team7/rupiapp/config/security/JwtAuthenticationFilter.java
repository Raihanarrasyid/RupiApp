package com.team7.rupiapp.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.team7.rupiapp.model.User;
import com.team7.rupiapp.service.JwtService;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private boolean isAllowedRequest(String requestURI) {
        return requestURI.equals("/auth/verify") ||
                requestURI.equals("/auth/verify/resend") ||
                requestURI.equals("/auth/set-password") ||
                requestURI.equals("/auth/set-pin") ||
                requestURI.equals("/auth/forgot-password") ||
                requestURI.equals("/auth/signout");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String username = jwtService.extractUsername(token);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    String requestURI = request.getRequestURI();

                    handleDisabledUser(userDetails, requestURI);
                    handleUserWithDefaultPassword(userDetails, requestURI);
                    handleUserWithoutPin(userDetails, requestURI);
                    handleLoginOtp(token, requestURI);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Failed to set user authentication: ", exception.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void handleDisabledUser(UserDetails userDetails, String requestURI) {
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            if (!user.isVerified() && !isAllowedRequest(requestURI)) {
                throw new AccessDeniedException("User is not verified");
            }
        }
    }

    private void handleUserWithDefaultPassword(UserDetails userDetails, String requestURI) {
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            if (user.isDefaultPassword() && !isAllowedRequest(requestURI)) {
                throw new AccessDeniedException("User has default password");
            }
        }
    }

    private void handleUserWithoutPin(UserDetails userDetails, String requestURI) {
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            if (user.getPin() == null && !isAllowedRequest(requestURI)) {
                throw new AccessDeniedException("User has no pin");
            }
        }
    }

    private void handleLoginOtp(String token, String requestURI) {
        if (!jwtService.isTokenEnabled(token) && !isAllowedRequest(requestURI)) {
            throw new AccessDeniedException("OTP is not verified"); // NOTE: custom response is same as user is not
                                                                    // verified
        }
    }
}
