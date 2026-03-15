package com.expert.entity;

import com.expert.enums.UserRole;
import com.expert.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime lastLoginTime;
    private Long createdAdminId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
