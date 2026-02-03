package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateUserDTO;
import com.example.CampusConnect.dto.LoginRequest;
import com.example.CampusConnect.dto.UpdateUserDTO;
import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.exceptions.AuthenticationException;
import com.example.CampusConnect.exceptions.DuplicateResourceException;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServicesImpl.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO registerUser(CreateUserDTO userDTO) {

        log.info("Attempting user registration | email={}", userDTO.getEmail());

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            log.warn("Registration failed - email already exists | email={}", userDTO.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        User user = modelMapper.map(userDTO, User.class);
        user.setRole(User.Role.valueOf(userDTO.getRole()));

        User savedUser = userRepository.save(user);

        log.info("User registered successfully | userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO loginUser(LoginRequest loginRequest) {

        log.info("Login attempt | email={}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed - email not found | email={}", loginRequest.getEmail());
                    return new AuthenticationException("Invalid email or password");
                });

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            log.warn("Login failed - incorrect password | email={}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());

        log.info("Login successful | userId={}, email={}", user.getId(), user.getEmail());

        return dto;
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) {

        log.info("Updating user | userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User update failed - not found | userId={}", userId);
                    return new ResourceNotFoundException("User not found: " + userId);
                });

        if (updateUserDTO.getName() != null)
            user.setName(updateUserDTO.getName());

        if (updateUserDTO.getPassword() != null)
            user.setPassword(updateUserDTO.getPassword());

        if (updateUserDTO.getRole() != null)
            user.setRole(User.Role.valueOf(updateUserDTO.getRole().trim().toUpperCase()));

        User saved = userRepository.save(user);

        log.info("User updated successfully | userId={}", saved.getId());

        UserDTO out = modelMapper.map(saved, UserDTO.class);
        out.setRole(saved.getRole().name());

        return out;
    }

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

    @Override
    public UserDTO getUserById(Long id) {

        log.info("Fetching user by ID | userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found | userId={}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());

        return dto;
    }

    @Override
    public List<UserDTO> getAllUsers() {

        log.info("Fetching all users");

        List<User> users = userRepository.findAll();

        log.info("Total users fetched | count={}", users.size());

        return users.stream()
                .map(user -> {
                    UserDTO dto = modelMapper.map(user, UserDTO.class);
                    dto.setRole(user.getRole().name());
                    return dto;
                })
                .toList();
    }
}
