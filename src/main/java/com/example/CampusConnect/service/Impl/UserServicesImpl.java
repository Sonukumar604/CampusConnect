package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateUserDTO;
import com.example.CampusConnect.dto.UpdateUserDTO;
import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.exceptions.DuplicateResourceException;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.oauth.model.AuthProvider;
import com.example.CampusConnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

        log.info("Registering user | email={}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = modelMapper.map(userDTO, User.class);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.valueOf(userDTO.getRole().trim().toUpperCase()));
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        log.info("User registered | userId={}", savedUser.getId());

        return mapToDTO(savedUser);
    }

    // ==========================
    // UPDATE USER
    // ==========================
    @Override
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + userId)
                );

        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }

        if (updateUserDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        if (updateUserDTO.getRole() != null) {
            user.setRole(Role.valueOf(updateUserDTO.getRole().trim().toUpperCase()));
        }

        User saved = userRepository.save(user);

        log.info("User updated | userId={}", saved.getId());

        return mapToDTO(saved);
    }

    // ==========================
    // DELETE USER
    // ==========================
    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + userId)
                );

        userRepository.delete(user);

        log.warn("User deleted | userId={}", userId);
    }

    // ==========================
    // GET USER BY ID
    // ==========================
    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + id)
                );

        return mapToDTO(user);
    }

    // ==========================
    // GET ALL USERS
    // ==========================
    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ==========================
    // GET USER BY EMAIL
    // ==========================
    @Override
    public UserDTO getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return mapToDTO(user);
    }

    // ==========================
    // OAUTH USER HANDLING
    // ==========================
    @Override
    public User findOrCreateOAuthUser(String email,
                                      String name,
                                      AuthProvider provider) {

        log.info("OAuth login | email={}, provider={}", email, provider);

        return userRepository.findByEmail(email)
                .map(existingUser -> {

                    if (existingUser.getProvider() == null) {
                        existingUser.setProvider(provider);
                        userRepository.save(existingUser);
                    }

                    return existingUser;
                })
                .orElseGet(() -> {

                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .provider(provider)
                            .role(Role.STUDENT)
                            .status(User.Status.ACTIVE)
                            // Required for NOT NULL password constraint
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .build();

                    User saved = userRepository.save(newUser);

                    log.info("OAuth user created | userId={}", saved.getId());

                    return saved;
                });
    }

    // ==========================
    // PRIVATE MAPPER
    // ==========================
    private UserDTO mapToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());
        return dto;
    }
}