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
import java.util.Optional;

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

    public Skill createSkill(Skill skillInput, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String skillName = skillInput.getName().trim();
        SkillType skillType = (skillInput.getType() == null) ? SkillType.HARD : skillInput.getType();

        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(skillName);

        Skill skillToLink;

        if (existingSkill.isPresent()) {
            skillToLink = existingSkill.get();
        } else {
            skillInput.setId(null);
            skillInput.setName(skillName);
            skillInput.setType(skillType);
            skillToLink = skillRepository.save(skillInput);
        }

        if (!user.getSkills().contains(skillToLink)) {
            user.getSkills().add(skillToLink);
            userRepository.save(user);
        }
        
        return skillToLink;
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElse(null);
    }

    public Skill updateSkill(Long skillId, Long userId, Skill skillDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Skill oldSkill = getSkillById(skillId);
        if (oldSkill == null) throw new RuntimeException("Skill not found");
        if (!user.getSkills().contains(oldSkill)) {
            throw new RuntimeException("Skill does not belong to user: " + userId);
        }
        user.getSkills().remove(oldSkill);

        Skill newSkillInput = new Skill();
        newSkillInput.setName(skillDetails.getName());
        newSkillInput.setType(skillDetails.getType());

        return createSkill(newSkillInput, userId);
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
    }
}