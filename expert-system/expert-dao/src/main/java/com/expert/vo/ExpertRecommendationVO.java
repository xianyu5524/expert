package com.expert.vo;

import com.expert.entity.Expert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertRecommendationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Expert expert;
    private Double matchScore;           // 总体匹配度 (0-1)
    private Map<String, Double> scoreDetails; // 各维度得分详情
    private String matchReason;          // 匹配理由描述
    private LocalDateTime lastInviteTime; // 最近邀请时间
    private Integer invitationCount;     // 历史邀请次数
}
