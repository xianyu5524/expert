package com.expert;

import com.expert.dto.RecommendationQueryDTO;
import com.expert.vo.ExpertRecommendationVO;

import java.util.List;

public interface RecommendationsService {
    List<ExpertRecommendationVO> getRecommendExpertList(RecommendationQueryDTO queryDTO);
}
