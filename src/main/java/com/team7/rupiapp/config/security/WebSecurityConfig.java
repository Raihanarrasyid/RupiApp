package com.team7.rupiapp.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final AuthEntryPointJwt authEntryPointJwt;
        private final OauthSuccessHandler oauthSuccessHandler;

        public WebSecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        AuthenticationProvider authenticationProvider,
                        AuthEntryPointJwt authEntryPointJwt,
                        OauthSuccessHandler oauthSuccessHandler) {
                this.authenticationProvider = authenticationProvider;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authEntryPointJwt = authEntryPointJwt;
                this.oauthSuccessHandler = oauthSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                // OidcUserService oidcUserService = new OidcUserService();
                return http.csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authEntryPointJwt))
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/docs*/**", "/swagger-ui/**").permitAll()
                                                .requestMatchers("/auth/**", "/login").permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                // .oauth2Login(oauth2 -> oauth2
                                //                 .loginPage("/login")
                                //                 .userInfoEndpoint(userInfo -> userInfo
                                //                                 .oidcUserService(oidcUserService))
                                //                 .successHandler(oauthSuccessHandler))
                                .build();
        }
}