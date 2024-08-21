package com.team7.rupiapp.specification;

import com.team7.rupiapp.model.Mutation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MutationSpecification implements Specification<Mutation> {
    private final UUID userId;
    private final Integer year;
    private final Integer month;
    private final String transactionPurpose;
    private final String transactionType;
    private final String mutationType;
    public MutationSpecification(UUID userId, Integer year, Integer month, String transactionPurpose,
                                 String transactionType, String mutationType) {
        this.userId = userId;
        this.year = year;
        this.month = month;
        this.transactionPurpose = transactionPurpose;
        this.transactionType = transactionType;
        this.mutationType = mutationType;
    }
    public Predicate toPredicate(Root<Mutation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Filter by user ID
        predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

        // Filter by date range if year and month are provided
        if (year != null && month != null) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            predicates.add(criteriaBuilder.between(root.get("createdAt"), startDateTime, endDateTime));
        }

        // Filter by transaction purpose
        if (transactionPurpose != null && !transactionPurpose.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("transactionPurpose"), transactionPurpose));
        }

        // Filter by transaction type
        if (transactionType != null && !transactionType.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("transactionType"), transactionType));
        }

        // Filter by mutation type
        if (mutationType != null && !mutationType.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("mutationType"), mutationType));
        }

        // Combine all predicates
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
