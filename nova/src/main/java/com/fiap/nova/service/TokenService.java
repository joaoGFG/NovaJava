package com.fiap.nova.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.fiap.nova.dto.TokenResponse;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public TokenResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();

        var role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("ROLE_USER");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("nova-api")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("role", role)
                .build();
        
        var token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenResponse(token, authentication.getName(), role);
    }
}