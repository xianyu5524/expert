package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectNameListResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String track;
    private String category;
    private String leaderName;
    private String stage;
    private String status;
    private String createdYear;

}
