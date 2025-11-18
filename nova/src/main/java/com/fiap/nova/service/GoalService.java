package com.fiap.nova.service;

import org.springframework.data.domain.Page;
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

    public Goal createGoal(Goal goal, Long userId) {
        return goalRepository.save(goal);
    }

    public List<Goal> listAll() {
        return goalRepository.findAll();
    }

    public Page<Goal> listAllPaginated(Pageable pageable) {
        return goalRepository.findAll(pageable);
    }

    public Goal getGoalById(Long id) {
        return goalRepository.findById(id)
                    .orElse(null);
    }

    public Goal updateGoal(Long id, Goal goalDetails) {
        Goal goal = getGoalById(id);
        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        if (goalDetails.getCategory() != null) {
            goal.setCategory(goalDetails.getCategory());
        }
        if (goalDetails.getStatus() != null) {
            goal.setStatus(goalDetails.getStatus());
        }
        return goalRepository.save(goal);
    }

    public void deleteGoal(Long id) {
        Goal goal = getGoalById(id);
        goalRepository.delete(goal);
    }
}
