package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.Goal;
import com.fiap.nova.service.GoalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goals")
@Tag(name = "Goals", description = "User goals management")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    @Operation(summary = "List all goals with pagination", description = "Returns paginated list of goals with HATEOAS links")
    public PagedModel<EntityModel<Goal>> getAll(
            @PageableDefault(size = 10, sort = "title") Pageable pageable,
            PagedResourcesAssembler<Goal> assembler) {
        log.info("Listing paginated goals - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        var page = goalService.listAllPaginated(pageable);
        return assembler.toModel(page, Goal::toEntityModel);
    }

    @GetMapping("/all")
    @Operation(summary = "List all goals without pagination", description = "Returns complete list of goals")
    public List<Goal> listAll() {
        log.info("Listing all goals");
        return goalService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new goal", description = "Creates a new goal for a user")
    public Goal createGoal(@RequestBody @Valid Goal goal, @RequestParam Long userId) {
        log.info("Creating goal: {} for userId: {}", goal, userId);
        return goalService.createGoal(goal, userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Returns a specific goal with HATEOAS links")
    public EntityModel<Goal> getById(@PathVariable Long id) {
        log.info("Getting goal with id: {}", id);
        var goal = goalService.getGoalById(id);
        return goal.toEntityModel();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Updates an existing goal")
    public EntityModel<Goal> updateGoal(@PathVariable Long id, @RequestBody @Valid Goal goal) {
        log.info("Updating goal with id: {}", id);
        var updatedGoal = goalService.updateGoal(id, goal);
        return updatedGoal.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", description = "Deletes a goal by ID")
    public void deleteGoal(@PathVariable Long id) {
        log.info("Deleting goal with id: {}", id);
        goalService.deleteGoal(id);
    }
}
