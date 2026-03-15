// UserPageRequestDTO.java
package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 页码
    private Integer page = 1;

    // 每页大小
    private Integer pageSize = 10;

    private String username;
    private String name;
    private String role;
    private String status;
}