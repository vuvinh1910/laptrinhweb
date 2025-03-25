package com.example.web_nhom_5.search.EntitySpecification;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.search.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification implements Specification<RoomEntity> {
    private final SearchCriteria searchCriteria;

    public RoomSpecification(SearchCriteria criteria) {
        super();
        this.searchCriteria=criteria;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<RoomEntity> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder){
        return switch (searchCriteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case GREATER_THAN ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LESS_THAN ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LIKE ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case STARTS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString() + "%");
            case ENDS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue().toString());
            case CONTAINS ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue() + "%");
            default -> null;
        };
    }
}