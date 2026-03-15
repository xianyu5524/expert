package com.expert.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RecommendationQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Integer topN;
    private Map<String, Object> filters;
}
