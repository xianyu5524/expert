package com.expert.controller;

import com.expert.ProjectService;
import com.expert.TemplateService;
import com.expert.annotation.RequireRole;
import com.expert.enums.UserRole;
import com.expert.dto.ExportRequestDTO;
import com.expert.dto.ProjectNameListRequestDTO;
import com.expert.dto.ProjectPageRequestDTO;
import com.expert.entity.Project;
import com.expert.result.PageResult;
import com.expert.result.Result;
import com.expert.utils.ExportUtils;
import com.expert.vo.ImportVO;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 项目管理模块
 */

@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectsController {
    @Autowired
    ProjectService projectService;

    @Autowired
    TemplateService templateService;

    @Autowired
    ExportUtils exportUtils;

    @GetMapping("/nameList")
    public Result<PageResult> getProjectNameList(ProjectNameListRequestDTO projectNameListRequestDTO) {
        log.info("获取项目名称列表：{}", projectNameListRequestDTO);
        PageResult pageResult = projectService.getNameList(projectNameListRequestDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @RequireRole({ UserRole.ADMIN, UserRole.SUPER_ADMIN })
    public Result saveProject(@RequestBody Project project) {
        log.info("添加项目：{}", project);
        projectService.saveProject(project);
        return Result.success();
    }

    @GetMapping("/pageList")
    public Result<PageResult> getProjectPageList(ProjectPageRequestDTO projectPageRequestDTO) {
        log.info("分页查询：{}", projectPageRequestDTO);
        PageResult pageResult = projectService.getPageList(projectPageRequestDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Project> getProjectById(@PathVariable Long id) {
        log.info("获取项目信息：{}", id);
        Project project = projectService.getProjectById(id);
        return Result.success(project);
    }

    @PutMapping
    @RequireRole({ UserRole.ADMIN, UserRole.SUPER_ADMIN })
    public Result updateProject(@RequestBody Project project) {
        log.info("更新项目信息：{}", project);
        projectService.updateProject(project);
        return Result.success();
    }

    @DeleteMapping
    @RequireRole({ UserRole.ADMIN, UserRole.SUPER_ADMIN })
    public Result deleteProject(@RequestBody List<Integer> ids) {
        log.info("删除项目：{}", ids);
        projectService.deleteProject(ids);
        return Result.success();
    }

    @GetMapping("/template")
    public void getTemplate(HttpServletResponse response) {
        log.info("获取项目批量导入模板");
        templateService.generateProjectTemplate(response);
    }

    @GetMapping("/export")
    @RequireRole({ UserRole.ADMIN, UserRole.SUPER_ADMIN })
    public void projectDataExport(@ModelAttribute ExportRequestDTO exportRequestDTO, HttpServletResponse response) {
        log.info("批量导出项目数据:{}", exportRequestDTO);
        // 设置响应头
        exportUtils.setupExcelResponse(response, "项目数据导出.xlsx");
        try (Workbook workbook = projectService.exportProjects(exportRequestDTO)) {
            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import")
    public Result<ImportVO> projectDataImport(@RequestParam("file") MultipartFile file) {
        log.info("批量导入项目数据，文件名: {}", file.getOriginalFilename());
        try {
            ImportVO importVO = projectService.importProjects(file);
            return Result.success(importVO);
        } catch (Exception e) {
            log.error("项目数据导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
