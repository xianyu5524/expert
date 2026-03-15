package com.expert.service;

import com.expert.context.BaseContext;
import com.expert.dto.InvitationRequestDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.entity.Expert;
import com.expert.entity.Invitation;
import com.expert.entity.Project;
import com.expert.enums.InvitationStatusEnum;
import com.expert.exception.BaseException;
import com.expert.impl.InvitationsServiceImpl;
import com.expert.mapper.ExpertsMapper;
import com.expert.mapper.InvitationsMapper;
import com.expert.mapper.ProjectsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvitationsServiceTest {

    @Mock
    private InvitationsMapper invitationsMapper;

    @Mock
    private ExpertsMapper expertsMapper;

    @Mock
    private ProjectsMapper projectsMapper;

    @InjectMocks
    private InvitationsServiceImpl invitationsService;

    @BeforeEach
    public void setUp() {
        BaseContext.setCurrentId(1L);
    }

    @Test
    public void testInvite_Success() {
        InvitationRequestDTO requestDTO = new InvitationRequestDTO();
        requestDTO.setExpertId(10L);
        requestDTO.setProjectId(20L);
        requestDTO.setNotes("Invite");

        when(projectsMapper.getProjectById(20L)).thenReturn(new Project());
        when(expertsMapper.getDetailExpertById(10L)).thenReturn(new Expert());

        invitationsService.invite(requestDTO);

        verify(invitationsMapper, times(1)).insert(any(Invitation.class));
    }

    @Test
    public void testInvite_ProjectNotFound() {
        InvitationRequestDTO requestDTO = new InvitationRequestDTO();
        requestDTO.setProjectId(20L);

        when(projectsMapper.getProjectById(20L)).thenReturn(null);

        assertThrows(BaseException.class, () -> invitationsService.invite(requestDTO));
    }

    @Test
    public void testRespond_Success() {
        Long inviteId = 1L;
        String status = "ACCEPTED";
        String notes = "Ok";

        invitationsService.respond(inviteId, status, notes);

        verify(invitationsMapper, times(1)).update(any(Invitation.class));
    }

    @Test
    public void testRespond_InvalidStatus() {
        Long inviteId = 1L;
        String status = "INVALID";

        assertThrows(BaseException.class, () -> invitationsService.respond(inviteId, status, null));
    }

    @Test
    public void testGetMyInvitations_Success() {
        BaseContext.setCurrentId(1L);
        when(expertsMapper.getExpertIdByUserId(1)).thenReturn(100);
        when(invitationsMapper.getInvitationById(100L)).thenReturn(Collections.emptyList());

        List<InvitationResponseDTO> result = invitationsService.getMyInvitations();

        assertNotNull(result);
        verify(invitationsMapper, times(1)).getInvitationById(100L);
    }

    @Test
    public void testGetMyInvitations_NotExpert() {
        BaseContext.setCurrentId(1L);
        when(expertsMapper.getExpertIdByUserId(1)).thenReturn(null);

        assertThrows(BaseException.class, () -> invitationsService.getMyInvitations());
    }
}
