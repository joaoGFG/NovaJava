package com.fiap.nova.filters;

import com.fiap.nova.model.SkillType;

public record SkillFilters(
    String name,
    SkillType type
) {}
