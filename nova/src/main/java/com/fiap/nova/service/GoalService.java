package com.fiap.nova.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiap.nova.model.Goal;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.GoalRepository;
import com.fiap.nova.repository.UserRepository;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public Page<Goal> listGoalsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Goal> userGoals = user.getGoals();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userGoals.size());
        
        List<Goal> pageContent = (start > userGoals.size()) ? List.of() : userGoals.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, userGoals.size());
    }

    public Goal createGoal(Goal goal, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Goal savedGoal = goalRepository.save(goal);
        
        user.getGoals().add(savedGoal);
        userRepository.save(user);
        
        return savedGoal;
    }

    public Goal getGoalById(Long id) {
        return goalRepository.findById(id)
                    .orElse(null);
    }

    public Goal updateGoal(Long goalId, Long userId, Goal goalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Goal goal = getGoalById(goalId);
        if (goal == null) {
            throw new RuntimeException("Goal not found");
        }

        if (!user.getGoals().contains(goal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }
        
        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        goal.setCategory(goalDetails.getCategory()); 
        goal.setStatus(goalDetails.getStatus());
        
        return goalRepository.save(goal);
    }

    public void deleteGoal(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Goal goal = getGoalById(goalId);      
        if (goal == null) {
             throw new RuntimeException("Goal not found");
        }

        if (!user.getGoals().contains(goal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }
        user.getGoals().remove(goal);
        userRepository.save(user);
        goalRepository.delete(goal);
    }
}