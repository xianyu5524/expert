package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AchievementResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String type;
    private String title;
    private LocalDate achievementDate;
    private String description;
}
