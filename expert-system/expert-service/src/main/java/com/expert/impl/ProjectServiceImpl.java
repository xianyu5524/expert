package com.expert.impl;

import com.expert.ProjectService;
import com.expert.dto.*;
import com.expert.entity.Project;
import com.expert.mapper.ProjectsMapper;
import com.expert.result.PageResult;
import com.expert.utils.ExportUtils;
import com.expert.utils.ImportUtils;
import com.expert.vo.ImportVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectsMapper projectsMapper;

    @Autowired
    ExportUtils exportUtils;

    @Autowired
    ImportUtils importUtils;

    @Override
    public PageResult getNameList(ProjectNameListRequestDTO projectNameListRequestDTO) {
        PageHelper.startPage(projectNameListRequestDTO.getPage(),projectNameListRequestDTO.getPageSize());
        Page<ProjectNameListResponseDTO> page=projectsMapper.getNameList(projectNameListRequestDTO.getKeyWord());
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void saveProject(Project project) {
        project.setCreatedTime(LocalDateTime.now());
        projectsMapper.insert(project);
    }

    @Override
    public PageResult getPageList(ProjectPageRequestDTO projectPageRequestDTO) {
        PageHelper.startPage(projectPageRequestDTO.getPage(),projectPageRequestDTO.getPageSize());
        Page<Project> pageList=projectsMapper.getPageList(projectPageRequestDTO);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public Project getProjectById(Long id) {
        return projectsMapper.getProjectById(id);
    }

    @Override
    public void updateProject(Project project) {
        project.setUpdatedTime(LocalDateTime.now());
        projectsMapper.update(project);
    }

    @Override
    public void deleteProject(List<Integer> ids) {
        projectsMapper.delete(ids);
    }

    @Override
    public Workbook exportProjects(ExportRequestDTO exportRequestDTO) {
        Workbook workbook=new XSSFWorkbook();

        // 创建数据sheet
        Sheet sheet = workbook.createSheet("项目数据");
        // 创建表头
        exportUtils.createProjectExportHeader(sheet,workbook);
        // 获取数据并填充
        List<Project> projects =projectsMapper.getProjectForExport(exportRequestDTO);
        exportUtils.fillProjectExportData(sheet, projects);

        return workbook;
    }

    @Override
    public ImportVO importProjects(MultipartFile file) {
        List<ImportErrorDTO> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet("项目信息");

            if (sheet == null) {
                throw new RuntimeException("Excel文件中缺少'项目信息'工作表");
            }

            List<Project> projects = importUtils.parseProjectSheet(sheet, errors);

            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);
                int rowNumber = i + 2;

                try {
                    importUtils.validateProjectData(project);
                    projectsMapper.insert(project);
                    successCount++;

                } catch (Exception e) {
                    failureCount++;
                    errors.add(ImportErrorDTO.builder()
                            .row(rowNumber)
                            .field("全部")
                            .value(project.toString())
                            .message(e.getMessage())
                            .build());
                }
            }

            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException("读取Excel文件失败", e);
        }

        return ImportVO.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .totalCount(successCount + failureCount)
                .errors(errors)
                .build();
    }


}
