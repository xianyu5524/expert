package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class InvitationHistoryQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long projectId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page;
    private Integer pageSize;
}
