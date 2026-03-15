package com.expert.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String track;
    private String category;
    private String leaderName;
    private String grade;
    private String major;
    private String stage;
    private String status;
    private String award;
    private String advisor;
    private String projectLevel;
    private String description;
    private String keywords;
    private Double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdYear;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
