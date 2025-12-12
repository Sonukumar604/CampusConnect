package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminUserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long userId);

    UserDTO updateUserRole(Long userId, String role);

    UserDTO blockUser(Long userId);
    UserDTO unblockUser(Long userId);

    void deleteUser(Long userId);

    Page<UserDTO> getAllUsersPaged(int page, int size, String sortBy, String sortDir);
}
