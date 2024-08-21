package com.team7.rupiapp.repository;

import com.team7.rupiapp.model.Destination;
import com.team7.rupiapp.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    List<Destination> findByUser(User user);

    Optional<Destination> findByUserAndAccountNumber(User user, String accountNumber);

    Optional<Destination> findByAccountNumber(String accountNumber);

    Page<Destination> findByUser(User user, Pageable pageable);

    Page<Destination> findByUserAndNameContainingIgnoreCaseOrAccountNumberContainingIgnoreCase(User user, String name,
            String number, Pageable pageable);
}
