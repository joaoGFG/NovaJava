package com.fiap.nova.service;

import com.fiap.nova.model.Goal;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.GoalRepository;
import com.fiap.nova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public Page<Goal> listGoalsByUser(Long userId, Pageable pageable) {
        if (userId == null) return Page.empty();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Goal> userGoals = user.getGoals();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userGoals.size());

        List<Goal> pageContent = (start > userGoals.size()) ? List.of() : userGoals.subList(start, end);

        return new PageImpl<>(pageContent, pageable, userGoals.size());
    }

    @Transactional
    public Goal createGoal(Goal goalInput, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String title = goalInput.getTitle() != null ? goalInput.getTitle().trim() : "Sem Título";
        
        Optional<Goal> existingGoal = goalRepository.findByTitleIgnoreCase(title);

        Goal goalToLink;

        if (existingGoal.isPresent()) {
            goalToLink = existingGoal.get();
        } else {
            goalInput.setId(null);
            goalInput.setTitle(title);
            goalToLink = goalRepository.save(goalInput);
        }

        if (!user.getGoals().contains(goalToLink)) {
            user.getGoals().add(goalToLink);
            userRepository.save(user);
        }

        return goalToLink;
    }

    public Goal getGoalById(Long id) {
        return goalRepository.findById(id).orElse(null);
    }

    @Transactional
    public Goal updateGoal(Long goalId, Long userId, Goal goalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Goal oldGoal = getGoalById(goalId);
        if (oldGoal == null) throw new RuntimeException("Goal not found");

        if (!user.getGoals().contains(oldGoal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }     
        user.getGoals().remove(oldGoal);     
        Goal newGoalInput = new Goal();
        newGoalInput.setTitle(goalDetails.getTitle());
        newGoalInput.setDescription(goalDetails.getDescription());
        newGoalInput.setCategory(goalDetails.getCategory());
        newGoalInput.setStatus(goalDetails.getStatus());

        return createGoal(newGoalInput, userId);
    }

    @Transactional
    public void deleteGoal(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Goal goal = getGoalById(goalId);
        if (goal == null) throw new RuntimeException("Goal not found");

        if (!user.getGoals().contains(goal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }

        user.getGoals().remove(goal);
        userRepository.save(user);
        
    }
}