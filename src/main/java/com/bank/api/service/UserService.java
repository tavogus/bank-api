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

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        user = userRepository.save(user);
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCpf()
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
} 