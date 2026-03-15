package com.expert.mapper;

import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.entity.Invitation;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InvitationsMapper {
    List<InvitationResponseDTO> getInvitationById(Long expertId);

    void delete(List<Integer> expertIds);

    Page<InvitationResponseDTO> getInvitationPageQueryByProjectId(InvitationHistoryQueryDTO queryDTO);

    @Insert("insert into invitations(expert_id, project_id, inviter_id, invite_time, status, notes, created_time) " +
            "values(#{expertId}, #{projectId}, #{inviterId}, #{inviteTime}, #{status}, #{notes}, #{createdTime})")
    void insert(Invitation invitation);

    @Update("update invitations set status=#{status}, response_time=#{responseTime}, notes=#{notes} where id=#{id}")
    void update(Invitation invitation);
}
