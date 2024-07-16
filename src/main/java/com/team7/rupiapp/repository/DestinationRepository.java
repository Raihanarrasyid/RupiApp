package com.team7.rupiapp.repository;

import com.team7.rupiapp.model.Destination;
import com.team7.rupiapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    List<Destination> findByUser(User user);

    Optional<Destination> findByUserAndAccountNumber(User user, String accountNumber);
}

