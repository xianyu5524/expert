package com.expert;

import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationRequestDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.result.PageResult;

import java.util.List;

public interface InvitationsService {

    /**
     * 发起邀请
     * @param invitationRequestDTO
     */
    void invite(InvitationRequestDTO invitationRequestDTO);

    /**
     * 专家回复邀请
     * @param id 邀请ID
     * @param status 状态 (ACCEPTED, REJECTED)
     * @param notes 备注
     */
    void respond(Long id, String status, String notes);

    /**
     * 获取专家的邀请列表
     * @return
     */
    List<InvitationResponseDTO> getMyInvitations();

    /**
     * 获取项目的邀请历史
     * @param queryDTO
     * @return
     */
    PageResult getProjectInvitationHistory(InvitationHistoryQueryDTO queryDTO);
}
