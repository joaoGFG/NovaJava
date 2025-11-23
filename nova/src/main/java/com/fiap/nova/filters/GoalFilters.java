package com.fiap.nova.filters;

public record GoalFilters(
    String title,
    String description,
    Long categoryId,
    Long statusId
) {}
