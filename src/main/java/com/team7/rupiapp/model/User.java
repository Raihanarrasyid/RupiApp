package com.team7.rupiapp.model;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String avatar;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String alias;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    private String password;

    @Column(nullable = false)
    private boolean verified = false;

    private String pin;

    @Column(nullable = false, unique = true, length = 10)
    private String accountNumber;

    private Double balance = 0.0;

    @Column(nullable = false)
    private boolean enabled = true;

    private boolean defaultPassword;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @PrePersist
    public void prePersist() {
        if (accountNumber == null) {
            int length = 10;
            Random random = new Random();
            String numbers = "0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(numbers.charAt(random.nextInt(numbers.length())));
            }

            accountNumber = sb.toString();
        }

        if (alias == null) {
            alias = fullName;
        }

        if (avatar == null) {
            avatar = "uploads/default.png";
        }
    }
}
