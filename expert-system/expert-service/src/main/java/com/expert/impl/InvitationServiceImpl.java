package com.expert.impl;

import com.expert.InvitationService;
import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.mapper.InvitationsMapper;
import com.expert.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl implements InvitationService {
    @Autowired
    InvitationsMapper invitationsMapper;
    @Override
    public PageResult getInvitationHistory(InvitationHistoryQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<InvitationResponseDTO> page=invitationsMapper.getInvitationPageQueryByProjectId(queryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
