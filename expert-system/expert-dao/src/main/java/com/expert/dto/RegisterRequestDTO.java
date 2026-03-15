package com.expert.dto;

import com.expert.enums.UserRole;
import com.expert.enums.UserStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RegisterRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status=UserStatus.PENDING;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
