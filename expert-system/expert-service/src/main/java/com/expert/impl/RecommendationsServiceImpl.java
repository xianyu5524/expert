package com.expert.impl;

import com.expert.RecommendationsService;
import com.expert.dto.RecommendationQueryDTO;
import com.expert.entity.Expert;
import com.expert.entity.Project;
import com.expert.enums.ExpertStatusEnum;
import com.expert.mapper.ExpertsMapper;
import com.expert.mapper.ProjectsMapper;
import com.expert.utils.RecommendationUtils;
import com.expert.vo.ExpertRecommendationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {

    @Autowired
    ProjectsMapper projectsMapper;

    @Autowired
    ExpertsMapper expertsMapper;

    @Autowired
    RecommendationUtils recommendationUtils;

    @Override
    public List<ExpertRecommendationVO> getRecommendExpertList(RecommendationQueryDTO query) {

        //通过项目和专家库比对获得推荐专家列表
        // 1. 获取项目信息
        Project project = projectsMapper.getProjectById(query.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        // 2. 获取所有已审核通过的候选专家
        List<Expert> candidates = expertsMapper.selectApprovedExperts(ExpertStatusEnum.APPROVED.name());
        // 3. 计算每个专家的匹配度
        return candidates.stream().map(expert -> recommendationUtils.calculateExpertRecommendation(expert, project))
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(query.getTopN() != null ? query.getTopN() : 50)
                .collect(Collectors.toList());
    }
}
