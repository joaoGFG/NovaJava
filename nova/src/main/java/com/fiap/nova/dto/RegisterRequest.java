package com.fiap.nova.dto;

import com.fiap.nova.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200)
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email
    @Size(max = 200)
    String email,
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    String password,
    
    @NotBlank(message = "Objetivo profissional é obrigatório")
    @Size(max = 255)
    String professionalGoal
) {
    public User toModel() {
        return User.builder()
                .nome(nome)
                .email(email)
                .password(password)
                .professionalGoal(professionalGoal)
                .role("ROLE_USER")
                .build();
    }
}
