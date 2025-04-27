package com.bank.api.controller;

import com.bank.api.dto.UserDTO;
import com.bank.api.dto.UserRegistrationDTO;
import com.bank.api.dto.security.AccountCredentialsDTO;
import com.bank.api.dto.security.TokenDTO;
import com.bank.api.service.AuthService;
import com.bank.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(userService.createUser(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AccountCredentialsDTO data) {
        return ResponseEntity.ok(authService.signin(data));
    }
} 