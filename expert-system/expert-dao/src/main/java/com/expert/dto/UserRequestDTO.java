// UserRequestDTO.java
package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
}