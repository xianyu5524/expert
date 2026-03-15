package com.expert.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 各维度得分实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DimensionScore implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;                 // 维度名称，如：领域匹配、职称等
    private String label;                // 维度显示标签
    private Double score;                // 该维度得分
    private String reason;               // 该维度匹配理由

}
