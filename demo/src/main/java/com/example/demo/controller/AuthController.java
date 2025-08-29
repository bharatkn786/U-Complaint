package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // -------------------- Signup --------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password cannot be null or empty"));
        }

        User savedUser = authService.register(user); // ✅ delegate to AuthService

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("id", savedUser.getId());
        response.put("role", savedUser.getRole());
        return ResponseEntity.ok(response);
    }

    // -------------------- Login --------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        User user = userService.getUserByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // 🔑 Explicitly convert Role enum to String
        String roleName = user.getRole().name(); // e.g. "ADMIN", "STUDENT", etc.

        // Generate JWT with email + role (keep "ROLE_" prefix if your security expects it)
        String token = jwtUtil.generateToken(user.getEmail(), roleName);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", roleName); // ✅ String instead of enum
        response.put("id", user.getId().toString());

        return ResponseEntity.ok(response);
    }


    // -------------------- Current User --------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // JWT subject = email
        return userRepository.findByEmail(email)
                .map(user -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", user.getId());
                    dto.put("email", user.getEmail());
                    dto.put("role", user.getRole());
                    dto.put("name", user.getName()); // fixed: use real name, not username(email)
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Nothing to invalidate in stateless JWT setup
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
