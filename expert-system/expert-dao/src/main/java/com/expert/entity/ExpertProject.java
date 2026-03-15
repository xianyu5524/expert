package com.expert.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpertProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long expertId;
    private Long projectId;
    private String role;
    private String status;
    private LocalDateTime createdTime;

    private boolean isNewProject;
    private Project newProject;
}
