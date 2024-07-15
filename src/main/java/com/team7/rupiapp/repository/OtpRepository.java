package com.team7.rupiapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.model.Otp;
import com.team7.rupiapp.model.User;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Otp findByCode(String code);

    Optional<Otp> findByUser(User user);

    Optional<Otp> findByUserAndType(User user, OtpType type);

}
