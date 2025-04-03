package com.bank.api.controller;

import com.bank.api.dto.UserDTO;
import com.bank.api.dto.UserRegistrationDTO;
import com.bank.api.security.JwtUtil;
import com.bank.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(userService.createUser(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.get("email"),
                loginRequest.get("password")
            )
        );

        String jwt = jwtUtil.generateToken(userService.loadUserByUsername(loginRequest.get("email")));
        
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        
        return ResponseEntity.ok(response);
    }
} 