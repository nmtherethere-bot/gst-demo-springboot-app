package com.gst.demo.authservice.controller;

import com.gst.demo.authservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // In a real application, you would use a user repository
    private Map<String, String> users = new HashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (users.containsKey(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        
        // Store hashed password
        users.put(username, passwordEncoder.encode(password));
        
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        // In a real application, you would validate against a database
        if (!users.containsKey(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
        
        String storedPassword = users.get(username);
        if (!passwordEncoder.matches(password, storedPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
        
        String token = jwtService.generateToken(username);
        
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String username = jwtService.validateTokenAndGetUsername(token);
            return ResponseEntity.ok(Map.of("username", username, "valid", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}