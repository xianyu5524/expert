package com.expert.impl;

import com.expert.ExpertsService;
import com.expert.context.BaseContext;
import com.expert.dto.*;
import com.expert.entity.*;
import com.expert.enums.UserRole;
import com.expert.enums.UserStatus;
import com.expert.mapper.*;
import com.expert.result.PageResult;
import com.expert.utils.ExportUtils;
import com.expert.utils.ImportUtils;
import com.expert.utils.UsernameBuildUtil;
import com.expert.vo.ExpertDetailResponseVO;
import com.expert.vo.ImportVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.expert.enums.ExpertStatusEnum;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class ExpertsServiceImpl implements ExpertsService {
    @Autowired
    ExpertsMapper expertsMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ProjectsMapper projectsMapper;

    @Autowired
    ExpertProjectMapper expertProjectMapper;

    @Autowired
    InvitationsMapper invitationsMapper;

    @Autowired
    AchievementsMapper achievementsMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    UsernameBuildUtil usernameBuildUtil;

    @Autowired
    ImportUtils importUtils;

    @Autowired
    ExportUtils exportUtils;

    @Override
    public PageResult pageQuery(ExpertPageRequestDTO expertPageRequestDTO) {
        PageHelper.startPage(expertPageRequestDTO.getPage(), expertPageRequestDTO.getPageSize());
        Page<ExpertPageResponseDTO> page = expertsMapper.page(expertPageRequestDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public ExpertDetailResponseVO expertQueryById(Long expertId) {
        Expert expert = expertsMapper.getDetailExpertById(expertId);
        List<ProjectResponseDTO> projects = projectsMapper.getProjectByExpertId(expertId);
        List<InvitationResponseDTO> invitations = invitationsMapper.getInvitationById(expertId);
        List<AchievementResponseDTO> achievements = achievementsMapper.getAchievementById(expertId);
        return new ExpertDetailResponseVO(expert, projects, invitations, achievements);
    }

    @Override
    @Transactional
    public String save(ExpertRequestDTO expertRequestDTO) {
        // 1. 生成用户名
        Expert expertSD = expertRequestDTO.getExpert();
        String username = usernameBuildUtil.generateUsername(expertSD);

        // 2. 创建系统用户账号
        SysUser sysUser = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .name(expertSD.getName())
                .email(expertSD.getEmail())
                .phone(expertSD.getPhone())
                .role(UserRole.EXPERT)
                .status(UserStatus.ACTIVE)
                .createdAdminId(BaseContext.getCurrentId())
                .createdTime(LocalDateTime.now())
                .build();

        sysUserMapper.insert(sysUser);
        // 3. 创建专家记录
        Expert expert = new Expert();
        BeanUtils.copyProperties(expertSD, expert);
        expert.setUserId(sysUser.getUserId());
        expert.setCreatedTime(LocalDateTime.now());
        expertsMapper.insert(expert);

        Long expertId = expert.getId();
        // 4. 保存成就记录
        List<Achievement> achievementsSD = expertRequestDTO.getAchievements();
        if (achievementsSD != null && !achievementsSD.isEmpty()) {
            for (Achievement achievement : achievementsSD) {
                achievement.setExpertId(expertId);
            }
            achievementsMapper.insert(achievementsSD);
        }
        // 5. 保存项目参与记录
        List<ExpertProject> expertProjectsSD = expertRequestDTO.getExpertProjects();
        if (expertProjectsSD != null && !expertProjectsSD.isEmpty()) {
            for (ExpertProject expertProject : expertProjectsSD) {
                if (expertProject.isNewProject()) {
                    // 创建新建项目
                    Project project = new Project();
                    BeanUtils.copyProperties(expertProject.getNewProject(), project);
                    project.setCreatedTime(LocalDateTime.now());
                    projectsMapper.insert(project);
                    expertProject.setProjectId(project.getId());
                }
                expertProject.setExpertId(expertId);
                expertProject.setCreatedTime(LocalDateTime.now());
            }

            expertProjectMapper.insert(expertProjectsSD);
        }
        // todo 6. 发送通知（可选，可以异步处理）将创建好的账号通过邮件或电话发送给用户，后续开发

        return username;
    }

    @Override
    @Transactional
    public void delete(List<Integer> expertIds) {
        // todo 1.删除提醒表关联的数据，暂时不做开发，等完成提醒功能后再开发
        // 2.删除与项目关联的数据
        expertProjectMapper.delete(expertIds);
        // 3.删除个人成就
        achievementsMapper.delete(expertIds);
        // 4.删除邀请记录
        invitationsMapper.delete(expertIds);
        // 5.删除专家关联的系统用户表中的数据
        List<Integer> userIds = expertsMapper.getSysUserId(expertIds);
        sysUserMapper.delete(userIds);
        // 6.删除专家数据
        expertsMapper.delete(expertIds);
    }

    @Override
    @Transactional
    public void update(ExpertRequestDTO expertRequestDTO) {
        Expert expert = expertRequestDTO.getExpert();
        List<Achievement> achievements = expertRequestDTO.getAchievements();
        List<ExpertProject> expertProjects = expertRequestDTO.getExpertProjects();

        expert.setUpdatedTime(LocalDateTime.now());
        expertsMapper.update(expert);

        Long id = expert.getId();
        if (achievements != null && !achievements.isEmpty()) {
            for (Achievement achievement : achievements) {
                achievement.setExpertId(id);
            }
            achievementsMapper.deleteById(id);
            achievementsMapper.insert(achievements);
        }

        if (expertProjects != null && !expertProjects.isEmpty()) {
            for (ExpertProject expertProject : expertProjects) {
                expertProject.setExpertId(id);
                expertProject.setCreatedTime(LocalDateTime.now());
            }
            expertProjectMapper.deleteByExpertId(id);
            expertProjectMapper.insert(expertProjects);
        }

    }

    @Override
    public Workbook exportExperts(ExportRequestDTO exportRequestDTO) {
        Workbook workbook = new XSSFWorkbook();
        // 创建数据sheet
        Sheet sheet = workbook.createSheet("专家数据");
        // 创建表头
        exportUtils.createExpertExportHeader(sheet, workbook);
        // 获取数据并填充
        List<Expert> experts = expertsMapper.getExpertsForExport(exportRequestDTO);
        exportUtils.fillExpertExportData(sheet, experts);
        return workbook;
    }

    @Override
    @Transactional
    public ImportVO importExperts(MultipartFile file) {
        List<ImportErrorDTO> errors = new ArrayList<>();

        int successCount = 0;
        int failureCount = 0;
        String accountFileUrl = null;

        try {
            // 读取Excel文件
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

            XSSFSheet sheet = workbook.getSheet("专家基本信息");

            if (sheet == null) {
                throw new RuntimeException("Excel文件中缺少'专家基本信息'工作表");
            }
            // 解析数据
            List<Expert> experts = importUtils.parseExpertSheet(sheet, errors);
            // 验证并保存数据

            ArrayList<ImportExpertSysResponseDTO> sysUserData = new ArrayList<>();
            for (int i = 0; i < experts.size(); i++) {
                Expert expert = experts.get(i);

                int rowNumber = i + 2;

                try {
                    // 验证数据
                    importUtils.validateExpertData(expert);

                    // 创建对应的系统用户并保存用户
                    String username = save(new ExpertRequestDTO(expert, null, null));
                    ImportExpertSysResponseDTO user = ImportExpertSysResponseDTO.builder()
                            .username(username)
                            .password("123456")
                            .name(expert.getName())
                            .phone(expert.getPhone())
                            .email(expert.getEmail())
                            .unit(expert.getUnit())
                            .department(expert.getDepartment())
                            .build();

                    sysUserData.add(user);

                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    errors.add(ImportErrorDTO.builder()
                            .row(rowNumber)
                            .field("全部")
                            .value(expert.toString())
                            .message(e.getMessage())
                            .build());
                    throw new RuntimeException("导入专家数据失败，行号: " + rowNumber
                            + ", 错误: " + e.getMessage());
                }
            }

            // 如果有成功导入的数据，生成账号信息Excel文件
            if (!sysUserData.isEmpty()) {
                accountFileUrl = importUtils.generateAccountInfoExcel(sysUserData);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("读取Excel文件失败", e);
        } catch (Exception e) {
            throw new RuntimeException("导入专家数据失败: " + e.getMessage(), e);
        }

        return ImportVO.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .totalCount(successCount + failureCount)
                .accountFileUrl(accountFileUrl)
                .errors(errors)
                .build();
    }

    @Override
    public void approveExpert(Long id) {
        // 校验专家是否存在
        Expert expert = expertsMapper.getDetailExpertById(id);
        if (expert == null) {
            throw new RuntimeException("专家不存在");
        }

        expertsMapper.updateStatus(id, ExpertStatusEnum.APPROVED.name(), LocalDateTime.now());
    }

    @Override
    public void rejectExpert(Long id, String reason) {
        // 校验专家是否存在
        Expert expert = expertsMapper.getDetailExpertById(id);
        if (expert == null) {
            throw new RuntimeException("专家不存在");
        }

        // todo 可以记录拒绝原因到数据库
        expertsMapper.updateStatus(id, ExpertStatusEnum.REJECTED.name(), LocalDateTime.now());
    }
}
