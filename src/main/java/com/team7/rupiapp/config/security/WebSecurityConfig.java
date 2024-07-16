package com.team7.rupiapp.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final AuthEntryPointJwt authEntryPointJwt;

        public WebSecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        AuthenticationProvider authenticationProvider,
                        AuthEntryPointJwt authEntryPointJwt) {
                this.authenticationProvider = authenticationProvider;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authEntryPointJwt = authEntryPointJwt;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http.csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authEntryPointJwt))
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/demo/create", "/demo/get-data", "/demo/error")
                                                .permitAll()
                                                .requestMatchers("/docs*/**", "/swagger-ui/**").permitAll()
                                                .requestMatchers("/auth/signout", "/auth/set-password", "/auth/set-pin").authenticated()
                                                .requestMatchers("/auth/**", "/login").permitAll()
                                                // .requestMatchers("/transfer/**").permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }
}