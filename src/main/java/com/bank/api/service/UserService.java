package com.bank.api.service;

import com.bank.api.dto.UserDTO;
import com.bank.api.dto.UserRegistrationDTO;
import com.bank.api.entity.User;
import com.bank.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public UserDTO createUser(UserRegistrationDTO userRegistrationDTO) {

        if (userRepository.existsByEmail(userRegistrationDTO.email())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByCpf(userRegistrationDTO.cpf())) {
            throw new RuntimeException("CPF already registered");
        }

        User user = new User();
        user.setEmail(userRegistrationDTO.email());
        user.setPassword(userRegistrationDTO.password());
        user.setFullName(userRegistrationDTO.fullName());
        user.setCpf(userRegistrationDTO.cpf());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCpf()
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 