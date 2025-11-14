package com.fiap.nova.model;

import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fiap.nova.controller.UserController;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
@Entity
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String email;

	private String password;

	private String professionalGoal;

	private String role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getUsername() {
		return email;
	}

	public EntityModel<User> toEntityModel() {
        return EntityModel.of(this)
                .add(linkTo(methodOn(UserController.class)
                        .getById(this.id)).withSelfRel());
    }
}
