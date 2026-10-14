package com.mit.fmp.service;



import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mit.fmp.dto.UserRequest;
import com.mit.fmp.dto.UserResponse;
import com.mit.fmp.entity.Users;
import com.mit.fmp.exception.UserNotFoundException;
import com.mit.fmp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered");
        }

        String role = request.getRole().trim().toUpperCase(Locale.ROOT);

        if (!List.of("FARMER", "BUYER").contains(role)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be FARMER or BUYER");
        }

        Users user = new Users();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        return convertToResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id));

        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id));

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email is already used by another user");
            }
        });

        String role = request.getRole().trim().toUpperCase(Locale.ROOT);

        if (!List.of("FARMER", "BUYER").contains(role)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be FARMER or BUYER");
        }

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setRole(role);

        // Update password only when a new password is provided
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            if (request.getPassword().length() < 8) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Password must contain at least 8 characters");
            }

            user.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        return convertToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id));

        userRepository.delete(user);
    }

    private UserResponse convertToResponse(Users user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}