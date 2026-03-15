package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ExpertPageResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String title;

    private String unit;

    private String field;

    private String expertType;

    private Integer weight;

    private Integer guidanceCount;

    private String status;

    private LocalDateTime lastInvite;

}
