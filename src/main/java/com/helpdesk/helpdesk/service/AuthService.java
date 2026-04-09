package com.helpdesk.helpdesk.service;

import com.helpdesk.helpdesk.config.JwtUtil;
import com.helpdesk.helpdesk.dto.LoginRequestDTO;
import com.helpdesk.helpdesk.dto.RegisterRequestDTO;
import com.helpdesk.helpdesk.entity.Role;
import com.helpdesk.helpdesk.entity.User;
import com.helpdesk.helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ─── Register ────────────────────────────────────────────────────
    public String register(RegisterRequestDTO dto) {
        // Check if username already exists
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "Error: Username already exists";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // ✅ encrypted
        user.setRole(Role.valueOf(dto.getRole())); // ROLE_ADMIN or ROLE_EMPLOYEE

        userRepository.save(user);
        return "User registered successfully";
    }

    // ─── Login ───────────────────────────────────────────────────────
    public String login(LoginRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check password
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate and return JWT token
        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
}