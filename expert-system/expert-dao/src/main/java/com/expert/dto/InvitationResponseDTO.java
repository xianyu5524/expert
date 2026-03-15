package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class InvitationResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String expertName;
    private String projectName;
    private String inviter;
    private String status;
    private String notes;
    private LocalDateTime inviteTime;
    private LocalDateTime responseTime;
}
