package com.expert.impl;

import com.expert.InvitationsService;
import com.expert.context.BaseContext;
import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationRequestDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.entity.Invitation;
import com.expert.enums.InvitationStatusEnum;
import com.expert.exception.BaseException;
import com.expert.mapper.ExpertsMapper;
import com.expert.mapper.InvitationsMapper;
import com.expert.mapper.ProjectsMapper;
import com.expert.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class InvitationsServiceImpl implements InvitationsService {

    @Autowired
    private InvitationsMapper invitationsMapper;

    @Autowired
    private ExpertsMapper expertsMapper;

    @Autowired
    private ProjectsMapper projectsMapper;

    @Override
    @Transactional
    public void invite(InvitationRequestDTO invitationRequestDTO) {
        // 1. 验证项目和专家是否存在
        if (projectsMapper.getProjectById(invitationRequestDTO.getProjectId()) == null) {
            throw new BaseException("项目不存在");
        }
        if (expertsMapper.getDetailExpertById(invitationRequestDTO.getExpertId()) == null) {
            throw new BaseException("专家不存在");
        }

        // 2. 创建邀请
        Invitation invitation = Invitation.builder()
                .expertId(invitationRequestDTO.getExpertId())
                .projectId(invitationRequestDTO.getProjectId())
                .inviterId(BaseContext.getCurrentId())
                .inviteTime(LocalDateTime.now())
                .status(InvitationStatusEnum.PENDING.name())
                .notes(invitationRequestDTO.getNotes())
                .createdTime(LocalDateTime.now())
                .build();

        invitationsMapper.insert(invitation);
    }

    @Override
    @Transactional
    public void respond(Long id, String status, String notes) {
        // 状态校验
        try {
            InvitationStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BaseException("无效的状态");
        }

        Invitation invitation = Invitation.builder()
                .id(id)
                .status(status)
                .responseTime(LocalDateTime.now())
                .notes(notes)
                .build();
        
        invitationsMapper.update(invitation);
    }

    @Override
    public List<InvitationResponseDTO> getMyInvitations() {
        Long userId = BaseContext.getCurrentId();
        // 需要根据UserId找到ExpertId
        Integer expertId = expertsMapper.getExpertIdByUserId(userId.intValue());
        if (expertId == null) {
            throw new BaseException("当前用户不是专家");
        }
        return invitationsMapper.getInvitationById(expertId.longValue());
    }

    @Override
    public PageResult getProjectInvitationHistory(InvitationHistoryQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<InvitationResponseDTO> page = invitationsMapper.getInvitationPageQueryByProjectId(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
