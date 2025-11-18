package com.fiap.nova.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiap.nova.filters.UserFilters;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.UserRepository;
import com.fiap.nova.specification.UserSpecification;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable, UserFilters filters) {
        Specification<User> spec = UserSpecification.build(filters);
        return userRepository.findAll(spec, pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public User updateUser(Long id, User userDetails) {
        User user = findById(id);
        user.setName(userDetails.getName());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}

