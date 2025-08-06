package com.cloudAwsDocker.service;

import com.cloudAwsDocker.dto.UserRequest;
import com.cloudAwsDocker.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    UserResponse getUserByUsername(String username);
}
