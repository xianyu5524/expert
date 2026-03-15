package com.expert.controller;

import com.expert.ExpertsService;
import com.expert.TemplateService;
import com.expert.dto.ExpertPageRequestDTO;
import com.expert.dto.ExpertRequestDTO;
import com.expert.dto.ExportRequestDTO;
import com.expert.result.PageResult;
import com.expert.result.Result;
import com.expert.utils.ExportUtils;
import com.expert.vo.ExpertDetailResponseVO;
import com.expert.vo.ImportVO;
import com.expert.annotation.RequireRole;
import com.expert.enums.UserRole;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专家管理模块
 */

@RestController
@RequestMapping("/experts")
@Slf4j
public class ExpertsController {
    @Autowired
    ExpertsService expertsService;

    @Autowired
    ExportUtils exportUtils;

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public Result<PageResult> pageQuery(ExpertPageRequestDTO expertPageRequestDTO) {
        log.info("专家信息分页查询:{}", expertPageRequestDTO);
        PageResult result = expertsService.pageQuery(expertPageRequestDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ExpertDetailResponseVO> getExpertDetail(@PathVariable Long id) {
        log.info("获取专家详细信息：{}", id);
        ExpertDetailResponseVO expertDetailResponseVO = expertsService.expertQueryById(id);
        return Result.success(expertDetailResponseVO);
    }

    @PostMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result saveExpert(@RequestBody @Validated ExpertRequestDTO expertRequestDTO) {
        log.info("添加专家：{}", expertRequestDTO);
        String username = expertsService.save(expertRequestDTO);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        return Result.success(data);
    }

    @DeleteMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result deleteExpert(@RequestBody List<Integer> ids) {
        log.info("删除专家：{}", ids);
        expertsService.delete(ids);
        return Result.success();
    }

    @PutMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result updateExpert(@RequestBody @Validated ExpertRequestDTO expertRequestDTO) {
        log.info("更新专家信息：{}", expertRequestDTO);
        expertsService.update(expertRequestDTO);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result approveExpert(@PathVariable Long id) {
        log.info("审核通过专家：{}", id);
        expertsService.approveExpert(id);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result rejectExpert(@PathVariable Long id, @RequestParam(required = false) String reason) {
        log.info("拒绝专家：{}, 原因: {}", id, reason);
        expertsService.rejectExpert(id, reason);
        return Result.success();
    }

    @GetMapping("/template")
    public void getTemplate(HttpServletResponse response) {
        log.info("获取专家批量导入模板");
        templateService.generateExpertTemplate(response);
    }

    @GetMapping("/export")
    public void expertDataExport(@ModelAttribute ExportRequestDTO exportRequestDTO, HttpServletResponse response) {
        log.info("批量导出专家数据:{}", exportRequestDTO);
        // 设置响应头
        exportUtils.setupExcelResponse(response, "专家数据导出.xlsx");
        try (Workbook workbook = expertsService.exportExperts(exportRequestDTO)) {
            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import")
    public Result<ImportVO> expertDataImport(@RequestParam MultipartFile file) {
        log.info("批量导入专家数据，文件名: {}", file.getOriginalFilename());
        try {
            ImportVO importVO = expertsService.importExperts(file);
            return Result.success(importVO);
        } catch (Exception e) {
            log.error("专家数据导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
