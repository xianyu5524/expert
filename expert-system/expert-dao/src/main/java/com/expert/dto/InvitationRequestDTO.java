package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class InvitationRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long expertId;
    private Long projectId;
    private String notes;
}
