package com.expert.mapper;

import com.expert.dto.ExpertPageRequestDTO;
import com.expert.dto.ExpertPageResponseDTO;
import com.expert.dto.ExportRequestDTO;
import com.expert.entity.Expert;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExpertsMapper {
    Page<ExpertPageResponseDTO> page(ExpertPageRequestDTO expertPageRequestDTO);

    @Select("SELECT * FROM experts where id=#{id}")
    Expert getDetailExpertById(Long id);

    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("INSERT INTO experts(user_id, name, gender, birth_date, title, unit, department, field, expert_type, weight, guidance_count, bank_account, bank_name, phone, email, avatar, introduction, status, last_invite, created_time, updated_time) "
            +
            "VALUES (#{userId}, #{name}, #{gender}, #{birthDate}, #{title}, #{unit}, #{department}, #{field}, #{expertType}, #{weight}, #{guidanceCount}, #{bankAccount}, #{bankName}, #{phone}, #{email}, #{avatar}, #{introduction}, #{status}, #{lastInvite}, #{createdTime}, #{updatedTime})")
    void insert(Expert expert);

    void delete(List<Integer> expertIds);

    List<Integer> getSysUserId(List<Integer> expertIds);

    @Update("update experts set name=#{name},gender=#{gender},birth_date=#{birthDate},title=#{title},unit=#{unit},department=#{department},field=#{field},expert_type=#{expertType},weight=#{weight},phone=#{phone},email=#{email},avatar=#{avatar},introduction=#{introduction},status=#{status},bank_account=#{bankAccount},bank_name=#{bankName},updated_time=#{updatedTime} where id=#{id}")
    void update(Expert expert);

    List<Expert> getExpertsForExport(ExportRequestDTO exportRequestDTO);

    @Select("select count(*)>0 from experts where phone=#{phone}")
    boolean existsByPhone(String phone);

    @Select("select count(*)>0 from experts where email=#{email}")
    boolean existsByEmail(String email);

    @Select("select * from experts where status=#{status}")
    List<Expert> selectApprovedExperts(String status);

    @Select("select id from experts where user_id=#{userId}")
    Integer getExpertIdByUserId(Integer userId);

    @Update("update experts set status=#{status}, updated_time=#{updatedTime} where id=#{id}")
    void updateStatus(Long id, String status, java.time.LocalDateTime updatedTime);
}
