package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.User.Role;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.AdminUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger log =
            LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users", users.size());

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.info("Fetching user with ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        log.info("User found with ID {}", userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUserRole(Long userId, String role) {
        log.info("Updating role for user ID {} to {}", userId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found while updating role. ID {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        try {
            Role newRole = Role.valueOf(role.trim().toUpperCase());
            user.setRole(newRole);
            log.info("Role parsed successfully: {}", newRole);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid role '{}' provided for user ID {}", role, userId);
            throw new IllegalArgumentException(
                    "Invalid role: " + role + ". Allowed: " +
                            String.join(", ",
                                    java.util.Arrays.stream(Role.values())
                                            .map(Enum::name)
                                            .toArray(String[]::new))
            );
        }

        User saved = userRepository.save(user);
        log.info("Role updated successfully for user ID {}", userId);

        UserDTO dto = modelMapper.map(saved, UserDTO.class);
        dto.setRole(saved.getRole().name());
        return dto;
    }

    @Override
    public UserDTO blockUser(Long userId) {
        log.info("Blocking user with ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found while blocking. ID {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setStatus(User.Status.BLOCKED);
        userRepository.save(user);

        log.info("User blocked successfully. ID {}", userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO unblockUser(Long userId) {
        log.info("Unblocking user with ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found while unblocking. ID {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        log.info("User unblocked successfully. ID {}", userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found while deleting. ID {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        userRepository.delete(user);
        log.info("User deleted successfully. ID {}", userId);
    }

    @Override
    public Page<UserDTO> getAllUsersPaged(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching paged users: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        log.info("Fetched {} users in current page", userPage.getNumberOfElements());

        return userPage.map(user -> modelMapper.map(user, UserDTO.class));
    }
}
