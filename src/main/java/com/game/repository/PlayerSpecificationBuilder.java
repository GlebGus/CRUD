package com.game.repository;
import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerSpecificationBuilder {

    private final List<SearchCriteria> params;


    public PlayerSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public PlayerSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<Player> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(PlayerSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(specs.get(i))
                    : Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}