package com.payflow.service;

import com.payflow.config.JwtService;
import com.payflow.dto.AuthDtos;
import com.payflow.entity.User;
import com.payflow.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository repo;
  private final JwtService jwtService;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthService(UserRepository repo, JwtService jwtService) {
    this.repo = repo;
    this.jwtService = jwtService;
  }

  public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
    if (repo.existsByEmail(req.email())) throw new IllegalArgumentException("Email already registered");
    User u = new User();
    u.setEmail(req.email());
    u.setPasswordHash(encoder.encode(req.password()));
    repo.save(u);
    return new AuthDtos.AuthResponse(jwtService.issueToken(u.getEmail()));
  }

  public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
    User u = repo.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!encoder.matches(req.password(), u.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
    return new AuthDtos.AuthResponse(jwtService.issueToken(u.getEmail()));
  }
}
