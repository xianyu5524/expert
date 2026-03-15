package com.expert.mapper;

import com.expert.dto.AchievementResponseDTO;
import com.expert.entity.Achievement;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AchievementsMapper {
    @Select("SELECT id,expert_id,type,title,achievement_date,description FROM achievements where expert_id=#{expertId}")
    List<AchievementResponseDTO> getAchievementById(Long expertId);

    void insert(List<Achievement> achievements);

    void delete(List<Integer> expertIds);

    @Delete("delete from achievements where expert_id=#{id}")
    void deleteById(Long id);
}
