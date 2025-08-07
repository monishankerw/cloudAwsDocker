package com.cloudAwsDocker.util;

import com.cloudAwsDocker.dto.UserResponse;
import com.cloudAwsDocker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        return response;
    }
}
