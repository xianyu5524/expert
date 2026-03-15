package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProjectResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String projectName;
    private String track;
    private String category;
    private String role;
    private String status;
    private String stage;
    private LocalDateTime createdTime;
}
