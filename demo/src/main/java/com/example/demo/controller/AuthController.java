package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // -------------------- Signup --------------------
    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password cannot be null or empty");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user via service
        User savedUser = userService.saveUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("id", savedUser.getId().toString());
        response.put("role", savedUser.getRole());
        return response;
    }

    // -------------------- Login --------------------
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            throw new RuntimeException("Email and password are required");
        }

        User user = userService.getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT with role
        String token = jwtUtil.generateToken(user.getEmail(), "ROLE_" + user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("id", user.getId().toString());
        return response;
    }
}
