package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateUserDTO;
import com.example.CampusConnect.dto.UpdateUserDTO;
import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.exceptions.DuplicateResourceException;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServicesImpl.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // ==========================
    // CREATE USER (Admin Use)
    // ==========================
    @Override
    public UserDTO registerUser(CreateUserDTO userDTO) {

        log.info("Attempting user registration | email={}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Registration failed - email already exists | email={}", userDTO.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        User user = modelMapper.map(userDTO, User.class);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.valueOf(userDTO.getRole().trim().toUpperCase()));
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        log.info("User registered successfully | userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return mapToDTO(savedUser);
    }

    // ==========================
    // UPDATE USER
    // ==========================
    @Override
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) {

        log.info("Updating user | userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User update failed - not found | userId={}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        if (updateUserDTO.getName() != null)
            user.setName(updateUserDTO.getName());

        if (updateUserDTO.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));

        if (updateUserDTO.getRole() != null)
            user.setRole(Role.valueOf(updateUserDTO.getRole().trim().toUpperCase()));

        User saved = userRepository.save(user);

        log.info("User updated successfully | userId={}", saved.getId());

        return mapToDTO(saved);
    }

    // ==========================
    // DELETE USER
    // ==========================
    @Override
    public void deleteUser(Long userId) {

        log.warn("Deleting user | userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Delete failed - user not found | userId={}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        userRepository.delete(user);

        log.warn("User deleted successfully | userId={}", userId);
    }

    // ==========================
    // GET USER BY ID
    // ==========================
    @Override
    public UserDTO getUserById(Long id) {

        log.info("Fetching user by ID | userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found | userId={}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        return mapToDTO(user);
    }

    // ==========================
    // GET ALL USERS
    // ==========================
    @Override
    public List<UserDTO> getAllUsers() {

        log.info("Fetching all users");

        List<User> users = userRepository.findAll();

        log.info("Total users fetched | count={}", users.size());

        return users.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ==========================
    // GET USER BY EMAIL
    // ==========================
    @Override
    public UserDTO getUserByEmail(String email) {

        log.info("Fetching user by email | email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found | email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        return mapToDTO(user);
    }

    // ==========================
    // PRIVATE MAPPER
    // ==========================
    private UserDTO mapToDTO(User user) {

        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());

        return dto;
    }
    @Override
    public User findOrCreateOAuthUser(String email, String name, String provider) {

        log.info("OAuth login attempt | email={}, provider={}", email, provider);

        return userRepository.findByEmail(email)
                .map(existingUser -> {

                    // If user exists but provider not set, update it
                    if (existingUser.getProvider() == null) {
                        existingUser.setProvider(provider);
                        userRepository.save(existingUser);
                    }

                    log.info("OAuth existing user found | userId={}", existingUser.getId());
                    return existingUser;
                })
                .orElseGet(() -> {

                    log.info("Creating new OAuth user | email={}", email);

                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .provider(provider)
                            .role(Role.STUDENT) // default role
                            .status(User.Status.ACTIVE)
                            // 🔐 Assign random encoded password (required for NOT NULL constraint)
                            .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                            .build();

                    User saved = userRepository.save(newUser);

                    log.info("OAuth user created successfully | userId={}", saved.getId());

                    return saved;
                });
    }
}
