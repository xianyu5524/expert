package com.expert.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 匹配度得分实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchScore implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double overallScore;         // 总体匹配度
    private List<DimensionScore> dimensionScores; // 各维度得分
    private String matchReason;          // 匹配理由

}
