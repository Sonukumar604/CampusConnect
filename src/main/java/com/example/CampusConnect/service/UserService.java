package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateUserDTO;
import com.example.CampusConnect.dto.LoginRequest;
import com.example.CampusConnect.dto.UpdateUserDTO;
import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.model.User;


import java.util.List;

public interface UserService {


    UserDTO registerUser(CreateUserDTO userDTO);

    UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO);

    void deleteUser(Long userId);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO getUserByEmail(String email);

    User findOrCreateOAuthUser(String email, String name, String provider);
}
