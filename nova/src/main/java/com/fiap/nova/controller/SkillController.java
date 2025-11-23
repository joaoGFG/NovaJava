package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.Skill;
import com.fiap.nova.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/skills")
@Tag(name = "Skills", description = "User skills management")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "List skills by user", description = "Returns paginated list of skills for a specific user")
    public PagedModel<EntityModel<Skill>> getSkillsByUser(
            @RequestParam Long userId, 
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            PagedResourcesAssembler<Skill> assembler) {
        
        log.info("Listing paginated skills for userId: {}", userId);
        var page = skillService.listSkillsByUser(userId, pageable);
        return assembler.toModel(page, Skill::toEntityModel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new skill", description = "Creates a new skill for a specific user")
    public Skill createSkill(@RequestBody @Valid Skill skill, @RequestParam Long userId) {
        log.info("Creating skill: {} for userId: {}", skill, userId);
        return skillService.createSkill(skill, userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update skill", description = "Updates an existing skill for a specific user")
    public EntityModel<Skill> updateSkill(@PathVariable Long id, @RequestParam Long userId, @RequestBody @Valid Skill skill) {
        log.info("Updating skill with id: {} for userId: {}", id, userId);
        var updatedSkill = skillService.updateSkill(id, userId, skill);
        return updatedSkill.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete skill", description = "Removes a skill from a specific user")
    public void deleteSkill(@PathVariable Long id, @RequestParam Long userId) {
        log.info("Deleting skill with id: {} for userId: {}", id, userId);
        skillService.deleteSkill(id, userId);
    }
}