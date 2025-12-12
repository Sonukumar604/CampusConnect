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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserService {


    private final  UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public UserDTO registerUser(CreateUserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }
        User user = modelMapper.map(userDTO, User.class);

        // ✅ Convert string role to Enum
        user.setRole(User.Role.valueOf(userDTO.getRole()));

        // ✅ Save user
        User savedUser = userRepository.save(user);

        // ✅ Convert Entity to DTO
        return modelMapper.map(savedUser, UserDTO.class);

    }

    private UserDTO mapToDTO(CreateUserDTO user) {
        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    @Override
    public UserDTO loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        // compare plain passwords (you will learn hashing later)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name()); // convert enum to string

        return dto;
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (updateUserDTO.getName() != null)
            user.setName(updateUserDTO.getName());

        if (updateUserDTO.getPassword() != null)
            user.setPassword(updateUserDTO.getPassword());

        if (updateUserDTO.getRole() != null)
            user.setRole(User.Role.valueOf(updateUserDTO.getRole().trim().toUpperCase()));

        User saved = userRepository.save(user);
        UserDTO out = modelMapper.map(saved, UserDTO.class);
        out.setRole(saved.getRole().name());
        return out;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Convert to DTO and return
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());
        return dto;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserDTO dto = modelMapper.map(user, UserDTO.class);
                    dto.setRole(user.getRole().name());
                    return dto;
                })
                .toList();
    }
}