package com.expert.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ImportExpertSysResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String unit;
    private String department;
}
