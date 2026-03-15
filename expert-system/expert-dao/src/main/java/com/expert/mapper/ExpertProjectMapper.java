package com.expert.mapper;

import com.expert.entity.ExpertProject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpertProjectMapper {
    void insert(List<ExpertProject> expertProjects);

    void delete(List<Integer> expertIds);

    @Delete("delete from expert_projects where expert_id=#{id}")
    void deleteByExpertId(Long id);

    List<ExpertProject> selectByExpertIdAndTrack(Long expertId, String track);
}
