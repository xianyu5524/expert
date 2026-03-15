package com.expert.mapper;

import com.expert.dto.ExportRequestDTO;
import com.expert.dto.ProjectNameListResponseDTO;
import com.expert.dto.ProjectPageRequestDTO;
import com.expert.dto.ProjectResponseDTO;
import com.expert.entity.Project;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectsMapper {
    List<ProjectResponseDTO> getProjectByExpertId(Long expertId);

    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("insert into projects(name, track, category, leader_name, grade, major, stage, award, advisor, project_level, description, keywords, budget, start_date, end_date, created_year, created_time, updated_time, status) "
            +
            "values (#{name}, #{track}, #{category}, #{leaderName}, #{grade}, #{major}, #{stage}, #{award}, #{advisor}, #{projectLevel}, #{description}, #{keywords}, #{budget}, #{startDate}, #{endDate}, #{createdYear}, #{createdTime}, #{updatedTime}, #{status})")
    void insert(Project project);

    Page<ProjectNameListResponseDTO> getNameList(String keyWord);

    Page<Project> getPageList(ProjectPageRequestDTO projectPageRequestDTO);

    @Select("select * from projects where id=#{id}")
    Project getProjectById(Long id);

    @Update("update projects set name=#{name},track=#{track},category=#{category},leader_name=#{leaderName},grade=#{grade},major=#{major},stage=#{stage},status=#{status},award=#{award},advisor=#{advisor},project_level=#{projectLevel},description=#{description},keywords=#{keywords},budget=#{budget},start_date=#{startDate},end_date=#{endDate},created_year=#{createdYear},updated_time=#{updatedTime} where id=#{id}")
    void update(Project project);

    void delete(List<Integer> ids);

    List<Project> getProjectForExport(ExportRequestDTO exportRequestDTO);

    @Select("select count(*)>0 from projects where name=#{name}")
    boolean existsByName(String name);
}
