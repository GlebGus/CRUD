package com.game.repository;
import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Date;


public class PlayerSpecification implements Specification<Player> {
    private SearchCriteria criteria;

    public PlayerSpecification(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Player> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder)
    {
        if (criteria.getOperation().equalsIgnoreCase("==")) {
            return builder.equal(root.get(criteria.getKey()), criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase(">=")) {
            return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (Integer) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.gt(root.get(criteria.getKey()), (Integer) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lt(root.get(criteria.getKey()), (Integer) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase("<=")) {
            return builder.lessThanOrEqualTo(root.get(criteria.getKey()), (Integer) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase("D>=")) {
            return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (Date) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase("D<=")) {
            return builder.lessThanOrEqualTo(root.get(criteria.getKey()), (Date) criteria.getValue());
        }
        if (criteria.getOperation().equalsIgnoreCase(":")) {
            return builder.like(root.get(criteria.getKey()),"%"+ criteria.getValue().toString()+"%");
        }
        if (criteria.getOperation().equalsIgnoreCase("true")) {
            if (criteria.getValue().toString().equalsIgnoreCase("true")) {
                return builder.isTrue(root.get(criteria.getKey()));
            }else if (criteria.getValue().toString().equalsIgnoreCase("false")) {
                return builder.isFalse(root.get(criteria.getKey()));
            }
        }
        return null;
    }

}