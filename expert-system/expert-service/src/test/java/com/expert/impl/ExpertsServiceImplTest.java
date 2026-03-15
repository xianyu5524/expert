package com.expert.impl;

import com.expert.context.BaseContext;
import com.expert.dto.ExpertRequestDTO;
import com.expert.entity.Achievement;
import com.expert.entity.Expert;
import com.expert.entity.ExpertProject;
import com.expert.entity.SysUser;
import com.expert.mapper.*;
import com.expert.utils.UsernameBuildUtil;
import com.expert.vo.ExpertDetailResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpertsServiceImplTest {

    @Mock
    private ExpertsMapper expertsMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private AchievementsMapper achievementsMapper;

    @Mock
    private ExpertProjectMapper expertProjectMapper;

    @Mock
    private ProjectsMapper projectsMapper;

    @Mock
    private InvitationsMapper invitationsMapper;

    @Mock
    private UsernameBuildUtil usernameBuildUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ExpertsServiceImpl expertsService;

    @BeforeEach
    void setUp() {
        // Set up thread local context if needed
        BaseContext.setCurrentId(1L);
    }

    @Test
    public void testSaveExpert() {
        // Prepare data
        ExpertRequestDTO requestDTO = new ExpertRequestDTO();
        Expert expert = new Expert();
        expert.setName("Test Expert");
        expert.setEmail("test@example.com");
        expert.setPhone("1234567890");
        requestDTO.setExpert(expert);

        List<Achievement> achievements = new ArrayList<>();
        Achievement achievement = new Achievement();
        achievement.setTitle("Achievement 1");
        achievements.add(achievement);
        requestDTO.setAchievements(achievements);

        // Mock behaviors
        when(usernameBuildUtil.generateUsername(any(Expert.class))).thenReturn("testuser");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setUserId(100L);
            return 1;
        }).when(sysUserMapper).insert(any(SysUser.class));

        doAnswer(invocation -> {
            Expert e = invocation.getArgument(0);
            e.setId(200L);
            return 1;
        }).when(expertsMapper).insert(any(Expert.class));

        // Execute
        String username = expertsService.save(requestDTO);

        // Verify
        assertEquals("testuser", username);
        verify(sysUserMapper, times(1)).insert(any(SysUser.class));
        verify(expertsMapper, times(1)).insert(any(Expert.class));
        verify(achievementsMapper, times(1)).insert(anyList());
    }

    @Test
    public void testUpdateExpert() {
        // Prepare data
        ExpertRequestDTO requestDTO = new ExpertRequestDTO();
        Expert expert = new Expert();
        expert.setId(200L);
        expert.setName("Updated Expert");
        requestDTO.setExpert(expert);

        List<Achievement> achievements = new ArrayList<>();
        Achievement achievement = new Achievement();
        achievement.setTitle("New Achievement");
        achievements.add(achievement);
        requestDTO.setAchievements(achievements);

        // Execute
        expertsService.update(requestDTO);

        // Verify
        verify(expertsMapper, times(1)).update(any(Expert.class));
        verify(achievementsMapper, times(1)).deleteById(200L);
        verify(achievementsMapper, times(1)).insert(anyList());
    }

    @Test
    public void testDeleteExpert() {
        // Prepare data
        List<Integer> expertIds = Arrays.asList(1, 2);
        when(expertsMapper.getSysUserId(expertIds)).thenReturn(Arrays.asList(10, 20));

        // Execute
        expertsService.delete(expertIds);

        // Verify
        verify(expertProjectMapper, times(1)).delete(expertIds);
        verify(achievementsMapper, times(1)).delete(expertIds);
        verify(invitationsMapper, times(1)).delete(expertIds);
        verify(sysUserMapper, times(1)).delete(anyList());
        verify(expertsMapper, times(1)).delete(expertIds);
    }

    @Test
    public void testExpertQueryById() {
        // Prepare data
        Long expertId = 1L;
        Expert expert = new Expert();
        expert.setId(expertId);
        expert.setName("Test Expert");

        when(expertsMapper.getDetailExpertById(expertId)).thenReturn(expert);
        when(projectsMapper.getProjectByExpertId(expertId)).thenReturn(Collections.emptyList());
        when(invitationsMapper.getInvitationById(expertId)).thenReturn(Collections.emptyList());
        when(achievementsMapper.getAchievementById(expertId)).thenReturn(Collections.emptyList());

        // Execute
        ExpertDetailResponseVO result = expertsService.expertQueryById(expertId);

        // Verify
        assertNotNull(result);
        assertEquals("Test Expert", result.getExpert().getName());
        assertNotNull(result.getProjects());
        assertNotNull(result.getInvitations());
        assertNotNull(result.getAchievements());
    }
}
