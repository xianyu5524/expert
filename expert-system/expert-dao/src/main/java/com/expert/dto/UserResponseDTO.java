// UserResponseDTO.java
package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}