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
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String roleInput = dto.getRole().trim().toUpperCase();
        if (!roleInput.startsWith("ROLE_")) {
            roleInput = "ROLE_" + roleInput;
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(roleInput));

        userRepository.save(user);
        return "User registered successfully";
    }

    // ─── Login ───────────────────────────────────────────────────────
    public String login(LoginRequestDTO dto) {
        String username = dto.getUsername().trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
}