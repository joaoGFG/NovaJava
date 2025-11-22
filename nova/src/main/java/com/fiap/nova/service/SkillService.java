package com.fiap.nova.service;

import com.fiap.nova.model.Skill;
import com.fiap.nova.model.SkillType;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.SkillRepository;
import com.fiap.nova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public Page<Skill> listSkillsByUser(Long userId, Pageable pageable) {
        if (userId == null) return Page.empty();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        List<Skill> userSkills = user.getSkills();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userSkills.size());
        
        List<Skill> pageContent = (start > userSkills.size()) ? List.of() : userSkills.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, userSkills.size());
    }

    public Skill createSkill(Skill skill, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        skill.setId(null);
        skill.setName(skill.getName().trim());
        if (skill.getType() == null) skill.setType(SkillType.HARD);
        
        Skill savedSkill = skillRepository.save(skill);
        
        user.getSkills().add(savedSkill);
        userRepository.save(user);
        
        return savedSkill;
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElse(null);
    }

    public Skill updateSkill(Long skillId, Long userId, Skill skillDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Skill skill = getSkillById(skillId);
        if (skill == null) throw new RuntimeException("Skill not found");
        
        if (!user.getSkills().contains(skill)) {
            throw new RuntimeException("Skill does not belong to user: " + userId);
        }
        
        skill.setName(skillDetails.getName().trim());
        if (skillDetails.getType() != null) {
            skill.setType(skillDetails.getType());
        }
        
        return skillRepository.save(skill);
    }
    
    public void deleteSkill(Long skillId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Skill skill = getSkillById(skillId);
        if (skill == null) throw new RuntimeException("Skill not found");
        
        if (!user.getSkills().contains(skill)) {
            throw new RuntimeException("Skill does not belong to user");
        }
        
        user.getSkills().remove(skill);
        userRepository.save(user);
        skillRepository.delete(skill);
    }
}