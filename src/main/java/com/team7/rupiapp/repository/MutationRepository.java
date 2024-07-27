package com.team7.rupiapp.repository;

import com.team7.rupiapp.model.Mutation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MutationRepository extends JpaRepository<Mutation, UUID> {
    List<Mutation> findByUserId(UUID userId);
    Page<Mutation> findByUserId(UUID userId, Pageable pageable);
}
