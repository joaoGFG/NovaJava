package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.AIInteraction;
import com.fiap.nova.service.AIInteractionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai-interactions")
@Tag(name = "AI Interactions", description = "User AI interactions history")
public class AIInteractionController {

    private final AIInteractionService aiInteractionService;

    public AIInteractionController(AIInteractionService aiInteractionService) {
        this.aiInteractionService = aiInteractionService;
    }

    @GetMapping
    @Operation(summary = "List all interactions with pagination", description = "Returns paginated list of AI interactions with HATEOAS links")
    public PagedModel<EntityModel<AIInteraction>> getAll(
            @PageableDefault(size = 10, sort = "interactionDate") Pageable pageable,
            PagedResourcesAssembler<AIInteraction> assembler) {
        log.info("Listing paginated AI interactions - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        var page = aiInteractionService.listAllPaginated(pageable);
        return assembler.toModel(page, AIInteraction::toEntityModel);
    }

    @GetMapping("/all")
    @Operation(summary = "List all interactions without pagination", description = "Returns complete list of AI interactions")
    public List<AIInteraction> listAll() {
        log.info("Listing all AI interactions");
        return aiInteractionService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new interaction", description = "Records a new AI interaction")
    public AIInteraction createInteraction(@RequestBody @Valid AIInteraction interaction) {
        log.info("Creating AI interaction: {}", interaction);
        return aiInteractionService.createInteraction(interaction);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get interaction by ID", description = "Returns a specific AI interaction with HATEOAS links")
    public EntityModel<AIInteraction> getById(@PathVariable Long id) {
        log.info("Getting AI interaction with id: {}", id);
        var interaction = aiInteractionService.getInteractionById(id);
        return interaction.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete interaction", description = "Deletes an AI interaction by ID")
    public void deleteInteraction(@PathVariable Long id) {
        log.info("Deleting AI interaction with id: {}", id);
        aiInteractionService.deleteInteraction(id);
    }
}
