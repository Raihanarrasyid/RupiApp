package com.team7.rupiapp.specification;

import com.team7.rupiapp.model.Mutation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

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
        Predicate predicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
        if (year != null) {
            Predicate yearPredicate = criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdAt")), year);
            predicate = criteriaBuilder.and(predicate, yearPredicate);
        }

        if (month != null) {
            Predicate monthPredicate = criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("createdAt")), month);
            predicate = criteriaBuilder.and(predicate, monthPredicate);
        }

        if (transactionPurpose != null && !transactionPurpose.isEmpty()) {
            Predicate purposePredicate = criteriaBuilder.equal(root.get("transactionPurpose"), transactionPurpose);
            predicate = criteriaBuilder.and(predicate, purposePredicate);
        }

        if (transactionType != null && !transactionType.isEmpty()) {
            Predicate typePredicate = criteriaBuilder.equal(root.get("transactionType"), transactionType);
            predicate = criteriaBuilder.and(predicate, typePredicate);
        }

        if (mutationType != null && !mutationType.isEmpty()) {
            Predicate mutationPredicate = criteriaBuilder.equal(root.get("mutationType"), mutationType);
            predicate = criteriaBuilder.and(predicate, mutationPredicate);
        }

        return predicate;
    }
}
