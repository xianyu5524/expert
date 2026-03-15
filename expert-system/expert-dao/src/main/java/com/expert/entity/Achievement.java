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
public class Achievement implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long expertId;
    private String type;
    private String title;
    private LocalDate achievementDate;
    private String description;
}
