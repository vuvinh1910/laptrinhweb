package com.example.web_nhom_5.search.EntitySpecification;

import com.example.web_nhom_5.entity.ServiceEntity;
import com.example.web_nhom_5.search.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecification implements Specification<ServiceEntity> {
    private SearchCriteria criteria;

    public ServiceSpecification(SearchCriteria criteria) {
        this.criteria=criteria;
    }


    @Override
    public Predicate toPredicate(@NotNull Root<ServiceEntity> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder)
    {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKeyword()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKeyword()), criteria.getValue());
            case GREATER_THAN ->
                    builder.greaterThan(root.get(criteria.getKeyword()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKeyword()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKeyword()), criteria.getValue().toString());
            case STARTS_WITH ->
                    builder.like(root.get(criteria.getKeyword()), criteria.getValue().toString() + "%");
            case ENDS_WITH ->
                    builder.like(root.get(criteria.getKeyword()), "%" + criteria.getValue().toString());
            case CONTAINS ->
                    builder.like(root.get(criteria.getKeyword()), "%" + criteria.getValue().toString() + "%");
            default -> null;
        };
    }
}
