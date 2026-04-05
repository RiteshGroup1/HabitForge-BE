package com.habitforge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @GetMapping("/google/login")
    public ResponseEntity<String> googleLogin() {
        String loginUrl = "http://localhost:8080/oauth2/authorization/google";
        return ResponseEntity.ok("Redirect to: " + loginUrl);
    }
    
    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback() {
        return ResponseEntity.ok("OAuth2 callback processed. Check response for JWT token.");
    }
}
