package com.expert.utils;

import com.expert.entity.Expert;
import com.expert.entity.Project;
import com.expert.enums.*;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
public class ExportUtils {
    /**
     * 设置Excel响应头
     */
    public  void setupExcelResponse(HttpServletResponse response, String fileName){
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFileName);
        } catch (UnsupportedEncodingException e) {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        }
    }

    // ==================== 专家导出相关方法 ====================

    /**
     * 创建专家导出表头
     */
    public  void createExpertExportHeader(Sheet sheet, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "姓名", "性别", "出生日期", "职称", "工作单位", "所在部门",
                "行业领域", "专家类型", "专家权重", "银行卡号", "开户行",
                "联系电话", "邮箱", "个人简介", "状态"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 15 * 256);
        }
    }

    /**
     * 填充专家导出数据
     */
    public  void fillExpertExportData(Sheet sheet, List<Expert> experts) {
        int rowNum = 1;
        for (Expert expert : experts) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(expert.getName());
            row.createCell(1).setCellValue(GenderEnum.format(expert.getGender()));
            row.createCell(2).setCellValue(expert.getBirthDate() != null ? expert.getBirthDate().toString() : "");
            row.createCell(3).setCellValue(expert.getTitle());
            row.createCell(4).setCellValue(expert.getUnit());
            row.createCell(5).setCellValue(expert.getDepartment());
            row.createCell(6).setCellValue(expert.getField());
            row.createCell(7).setCellValue(ExpertTypeEnum.format(expert.getExpertType()));
            row.createCell(8).setCellValue(expert.getWeight() != null ? expert.getWeight().toString() : "");
            row.createCell(9).setCellValue(expert.getBankAccount());
            row.createCell(10).setCellValue(expert.getBankName());
            row.createCell(11).setCellValue(expert.getPhone());
            row.createCell(12).setCellValue(expert.getEmail());
            row.createCell(13).setCellValue(expert.getIntroduction());
            row.createCell(14).setCellValue(ExpertStatusEnum.format(expert.getStatus()));
        }
    }

    // ==================== 项目导出相关方法 ====================

    /**
     * 创建项目导出表头
     */
    public  void createProjectExportHeader(Sheet sheet, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "项目名称", "项目赛道", "项目组别", "负责人姓名", "年级", "专业",
                "项目阶段", "项目状态", "获奖情况", "指导老师", "项目等级", "创建年份"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 15 * 256);
        }
    }

    /**
     * 填充项目导出数据
     */
    public  void fillProjectExportData(Sheet sheet, List<Project> projects) {
        int rowNum = 1;
        for (Project project : projects) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(project.getName());
            row.createCell(1).setCellValue(ProjectTrackEnum.format(project.getTrack()));
            row.createCell(2).setCellValue(ProjectCategoryEnum.format(project.getCategory()));
            row.createCell(3).setCellValue(project.getLeaderName());
            row.createCell(4).setCellValue(project.getGrade());
            row.createCell(5).setCellValue(project.getMajor());
            row.createCell(6).setCellValue(ProjectStageEnum.format(project.getStage()));
            row.createCell(7).setCellValue(ProjectStatusEnum.format(project.getStatus()));
            row.createCell(8).setCellValue(ProjectAwardEnum.format(project.getAward()));
            row.createCell(9).setCellValue(project.getAdvisor());
            row.createCell(10).setCellValue(ProjectLevelEnum.format(project.getProjectLevel()));
            row.createCell(11).setCellValue(project.getCreatedYear());
        }
    }

    // ==================== 通用方法 ====================

    /**
     * 创建表头样式
     */
    private  CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
