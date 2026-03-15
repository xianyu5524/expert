package com.expert.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 匹配度得分实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invitation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long expertId;
    private Long projectId;
    private Long inviterId;
    private LocalDateTime inviteTime;
    private String status;
    private LocalDateTime responseTime;
    private String notes;
    private LocalDateTime createdTime;
}
