package com.fiap.nova.specification;

import com.fiap.nova.filters.SkillFilters;
import com.fiap.nova.model.Skill;
import com.fiap.nova.model.SkillType;
import org.springframework.data.jpa.domain.Specification;

public class SkillSpecification {

    public static Specification<Skill> withFilters(SkillFilters filters) {
        return Specification
                .where(byName(filters.name()))
                .and(byType(filters.type()));
    }

    private static Specification<Skill> byName(String name) {
        if (name == null || name.isBlank()) return null;
        
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Skill> byType(SkillType type) {
        if (type == null) return null;
        
        return (root, query, cb) -> 
            cb.equal(root.get("type"), type);
    }
}