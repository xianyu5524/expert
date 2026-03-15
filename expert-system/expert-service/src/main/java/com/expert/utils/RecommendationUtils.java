package com.expert.utils;

import com.expert.dto.AchievementResponseDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.entity.*;
import com.expert.mapper.AchievementsMapper;
import com.expert.mapper.ExpertProjectMapper;
import com.expert.mapper.InvitationsMapper;
import com.expert.mapper.ProjectsMapper;
import com.expert.vo.ExpertRecommendationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecommendationUtils {

    @Autowired
    InvitationsMapper invitationsMapper;

    @Autowired
    ExpertProjectMapper expertProjectMapper;

    @Autowired
    ProjectsMapper projectsMapper;

    @Autowired
    AchievementsMapper achievementsMapper;

    // 推荐算法权重配置（后续可以从数据库配置表读取）
    private final Map<String, Double> defaultWeights = new HashMap<>();

    public RecommendationUtils() {
        defaultWeights.put("fieldMatch", 0.3);// 领域相关度权重
        defaultWeights.put("title", 0.2);// 职称权重
        defaultWeights.put("experience", 0.25);// 相关项目经验权重
        defaultWeights.put("achievement", 0.15);// 个人成就权重
        defaultWeights.put("activity", 0.05);// 活跃度权重
        defaultWeights.put("expertLevel", 0.05);// 专家等级权重
    }

    // 构建专家返回视图
    public ExpertRecommendationVO calculateExpertRecommendation(Expert expert, Project project) {
        MatchScore matchScore = calculateMatchScore(expert, project);

        // 获取专家邀请统计
        List<InvitationResponseDTO> invitations = invitationsMapper.getInvitationById(expert.getId());

        LocalDateTime lastInviteTime = invitations.stream()
                .map(InvitationResponseDTO::getInviteTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return ExpertRecommendationVO.builder()
                .expert(expert)
                .matchScore(matchScore.getOverallScore())
                .scoreDetails(matchScore.getDimensionScores().stream()
                        .collect(Collectors.toMap(DimensionScore::getName, DimensionScore::getScore)))
                .matchReason(matchScore.getMatchReason())
                .lastInviteTime(lastInviteTime)
                .invitationCount(invitations.size())
                .build();
    }

    // 计算匹配度
    private MatchScore calculateMatchScore(Expert expert, Project project) {
        // 计算各维度得分

        // 领域匹配度得分
        double fieldScore = calculateFieldMatchScore(expert.getField(), project.getTrack(), project.getCategory());
        // 专家职称得分
        double titleScore = calculateTitleScore(expert.getTitle());
        // 项目经验得分
        double experienceScore = calculateExperienceScore(expert.getId(), project);
        // 专家成就得分
        double achievementScore = calculateAchievementScore(expert.getId());
        // 专家活跃得分
        double activityScore = calculateActivityScore(expert);
        // 专家权重得分
        double expertLevelScore = calculateExpertLevelScore(expert.getWeight());

        // 加权计算总分

        double totalScore = fieldScore * defaultWeights.get("fieldMatch") +
                titleScore * defaultWeights.get("title") +
                experienceScore * defaultWeights.get("experience") +
                achievementScore * defaultWeights.get("achievement") +
                activityScore * defaultWeights.get("activity") +
                expertLevelScore * defaultWeights.get("expertLevel");

        // 构建维度得分详情

        List<DimensionScore> dimensionScores = Arrays.asList(
                createDimensionScore("field", "领域匹配", fieldScore,
                        String.format("专家领域 [%s] 与项目赛道 [%s] 的匹配度", expert.getField(), project.getTrack())),
                createDimensionScore("title", "职称等级", titleScore,
                        String.format("专家职称 [%s] 的等级评分", expert.getTitle())),
                createDimensionScore("experience", "项目经验", experienceScore,
                        "基于类似项目参与经验的评分"),
                createDimensionScore("achievement", "成就荣誉", achievementScore,
                        "基于专家成就和荣誉的评分"),
                createDimensionScore("activity", "活跃度", activityScore,
                        "基于近期活动和指导次数的评分"),
                createDimensionScore("expertLevel", "专家等级", expertLevelScore,
                        String.format("专家权重等级 %d/5", expert.getWeight())));

        // 生成匹配理由
        String matchReason = generateMatchReason(dimensionScores, totalScore);

        return MatchScore.builder()
                .overallScore(totalScore)
                .dimensionScores(dimensionScores)
                .matchReason(matchReason)
                .build();
    }

    // 工具方法

    // 构建维度得分详情
    private DimensionScore createDimensionScore(String name, String label, double score, String reason) {
        DimensionScore dimension = new DimensionScore();
        dimension.setName(name);
        dimension.setLabel(label);
        dimension.setScore(score);
        dimension.setReason(reason);
        return dimension;
    }

    // 算法：领域匹配
    private double calculateFieldMatchScore(String expertField, String projectTrack, String projectCategory) {
        if (expertField == null || projectTrack == null) {
            return 0.0;
        }

        // 简单的关键词匹配算法
        Set<String> expertFields = Arrays.stream(expertField.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> projectKeywords = new HashSet<>();
        projectKeywords.add(projectTrack.toLowerCase());
        if (projectCategory != null) {
            projectKeywords.add(projectCategory.toLowerCase());
        }

        // 计算Jaccard相似度
        Set<String> intersection = new HashSet<>(expertFields);
        intersection.retainAll(projectKeywords);

        Set<String> union = new HashSet<>(expertFields);
        union.addAll(projectKeywords);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    // 职称评分
    private double calculateTitleScore(String title) {
        Map<String, Double> titleScores = new HashMap<>();
        titleScores.put("教授", 1.0);
        titleScores.put("特聘教授", 1.0);
        titleScores.put("副教授", 0.8);
        titleScores.put("副研究员", 0.8);
        titleScores.put("讲师", 0.6);
        titleScores.put("助理研究员", 0.6);
        titleScores.put("工程师", 0.5);

        return titleScores.getOrDefault(title, 0.3);
    }

    // 算法：专家项目经历得分
    private double calculateExperienceScore(Long expertId, Project project) {
        // 查询专家参与过的相关项目
        List<ExpertProject> relatedProjects = expertProjectMapper.selectByExpertIdAndTrack(expertId,
                project.getTrack().toUpperCase());

        if (relatedProjects.isEmpty()) {
            return 0.0;
        }

        // 基于项目数量和项目阶段计算经验分
        double quantityScore = Math.min(relatedProjects.size() / 10.0, 1.0);

        // 考虑项目阶段的重要性（国赛 > 省赛 > 校赛）
        double stageScore = relatedProjects.stream()
                .mapToDouble(ep -> {
                    Project p = projectsMapper.getProjectById(ep.getProjectId());
                    return p != null ? getStageWeight(p.getStage()) : 0.0;
                })
                .average()
                .orElse(0.0);

        return (quantityScore * 0.6 + stageScore * 0.4);
    }

    // 获取项目阶段的权重
    private double getStageWeight(String stage) {
        Map<String, Double> stageWeights = new HashMap<>();
        stageWeights.put("NATIONAL", 1.0);
        stageWeights.put("PROVINCIAL", 0.7);
        stageWeights.put("SCHOOL", 0.4);

        return stageWeights.getOrDefault(stage, 0.3);
    }

    // 算法：专家成就得分
    private double calculateAchievementScore(Long expertId) {
        // 查询专家成就数量
        List<AchievementResponseDTO> achievements = achievementsMapper.getAchievementById(expertId);
        return Math.min(achievements.size() / 5.0, 1.0);
    }

    // 算法：专家活跃度得分
    private double calculateActivityScore(Expert expert) {
        double guidanceScore = Math.min(expert.getGuidanceCount() / 20.0, 1.0);

        // 最近邀请时间权重（越近越高）
        double recencyScore = 0.0;
        if (expert.getLastInvite() != null) {
            long daysSinceLastInvite = ChronoUnit.DAYS.between(expert.getLastInvite(), LocalDateTime.now());
            recencyScore = Math.max(0, 1.0 - (daysSinceLastInvite / 90.0)); // 90天内有效
        }

        return (guidanceScore * 0.7 + recencyScore * 0.3);
    }

    // 算法：计算专家权重得分
    private double calculateExpertLevelScore(Integer weight) {
        return weight != null ? weight / 5.0 : 0.2;
    }

    // 生成匹配原因
    private String generateMatchReason(List<DimensionScore> dimensions, double totalScore) {
        // 找到得分最高的两个维度
        dimensions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        StringBuilder reason = new StringBuilder("匹配原因：");
        if (totalScore >= 0.8) {
            reason.append("高度匹配，");
        } else if (totalScore >= 0.6) {
            reason.append("良好匹配，");
        } else {
            reason.append("一般匹配，");
        }

        for (int i = 0; i < Math.min(2, dimensions.size()); i++) {
            DimensionScore dimension = dimensions.get(i);
            if (dimension.getScore() > 0) {
                reason.append(dimension.getLabel())
                        .append("(")
                        .append(String.format("%.0f", dimension.getScore() * 100))
                        .append("%)");
                if (i == 0 && dimensions.size() > 1) {
                    reason.append("、");
                }
            }
        }
        reason.append("方面表现突出");

        return reason.toString();
    }

}
