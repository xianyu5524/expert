package com.expert.impl;

import com.expert.TemplateService;
import com.expert.utils.ExportUtils;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    ExportUtils exportUtils;

    /**
     * 生成专家导入模板
     */
    public void generateExpertTemplate(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 设置响应头
            exportUtils.setupExcelResponse(response, "专家信息导入模板.xlsx");

            // 创建各个sheet（去掉单独的填写说明sheet）
            createExpertBasicInfoSheet(workbook);
            createProjectParticipationSheet(workbook);
            createAchievementSheet(workbook);

            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("生成专家模板失败", e);
        }
    }

    /**
     * 生成项目导入模板
     */
    public void generateProjectTemplate(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            exportUtils.setupExcelResponse(response, "项目信息导入模板.xlsx");

            createProjectInfoSheet(workbook);

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("生成项目模板失败", e);
        }
    }


    /**
     * 创建专家基本信息sheet
     */
    private void createExpertBasicInfoSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("专家基本信息");

        // 创建说明样式
        CellStyle noteStyle = createNoteStyle(workbook);
        // 添加说明行
        addExpertBasicInfoInstructions(sheet, noteStyle);
        // 创建表头样式
        CellStyle headerStyle = createHeaderStyle(workbook);

        // 创建表头（从第4行开始，因为前面有3行说明）
        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "姓名*", "性别*", "出生日期", "职称*", "工作单位*", "所在部门",
                "行业领域", "专家类型*", "专家权重", "银行卡号", "开户行",
                "联系电话*", "邮箱*", "个人简介"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 15 * 256);
        }

        // 添加数据验证
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // 性别验证
        addDropdownValidation(sheet, validationHelper, 4, 1000, 1,
                new String[]{"男", "女"});

        // 专家类型验证
        addDropdownValidation(sheet, validationHelper, 4, 1000, 7,
                new String[]{"教育专家", "企业专家"});

        // 专家权重验证
        addDropdownValidation(sheet, validationHelper, 4, 1000, 8,
                new String[]{"1", "2", "3", "4", "5"});

        // 添加示例数据
        addExampleData(sheet, 4, new String[]{
                "张三", "男", "1990-01-01", "教授", "某某大学", "计算机学院",
                "人工智能,机器学习", "教育专家", "3", "123456789012345678", "中国工商银行",
                "13800138000", "zhangsan@example.com", "个人简介示例"
        });
    }

    /**
     * 添加专家基本信息说明
     */
    private void addExpertBasicInfoInstructions(Sheet sheet, CellStyle noteStyle){
        // 第一行：标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("专家基本信息填写说明");
        titleCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

        // 第二行：基本说明
        Row instructionRow1 = sheet.createRow(1);
        Cell instructionCell1 = instructionRow1.createCell(0);
        instructionCell1.setCellValue("标*的字段为必填项 | 性别：男/女 | 专家类型：教育专家/企业专家 | 专家权重：1-5（数字越大权重越高）");
        instructionCell1.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));

        // 第三行：格式说明
        Row instructionRow2 = sheet.createRow(2);
        Cell instructionCell2 = instructionRow2.createCell(0);
        instructionCell2.setCellValue("日期格式：YYYY-MM-DD（如：1990-01-01） | 行业领域：多个领域用逗号分隔 | 请勿修改表格结构");
        instructionCell2.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 13));
    }

    /**
     * 创建项目参与sheet
     */
    private void createProjectParticipationSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("项目参与");

        // 添加说明
        CellStyle noteStyle = createNoteStyle(workbook);
        addProjectParticipationInstructions(sheet, noteStyle);

        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(3); // 从第4行开始

        String[] headers = {"专家姓名*", "项目名称*", "参与角色*", "参与状态*"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 参与状态验证
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        addDropdownValidation(sheet, validationHelper, 4, 1000, 3,
                new String[]{"参与中", "已结束"});

        // 示例数据
        addExampleData(sheet, 4, new String[]{
                "张三", "人工智能研究项目", "评审专家", "参与中"
        });
    }

    /**
     * 添加项目参与说明
     */
    private void addProjectParticipationInstructions(Sheet sheet, CellStyle noteStyle) {
        // 第一行：标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目参与信息填写说明");
        titleCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 第二行：说明
        Row instructionRow = sheet.createRow(1);
        Cell instructionCell = instructionRow.createCell(0);
        instructionCell.setCellValue("标*的字段为必填项 | 专家姓名必须与基本信息表中的姓名一致 | 参与状态：参与中/已结束");
        instructionCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        // 第三行：注意事项
        Row noteRow = sheet.createRow(2);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("项目名称可以是现有项目或新建项目 | 请确保数据格式正确");
        noteCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
    }

    /**
     * 创建个人成就sheet
     */
    private void createAchievementSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("个人成就");

        // 添加说明
        CellStyle noteStyle = createNoteStyle(workbook);
        addAchievementInstructions(sheet, noteStyle);

        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(3); // 从第4行开始

        String[] headers = {"专家姓名*", "成就标题*", "获得日期*", "详细描述"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 示例数据
        addExampleData(sheet, 4, new String[]{
                "张三", "优秀教师奖", "2023-01-01", "获得校级优秀教师称号"
        });
    }

    /**
     * 添加个人成就说明
     */
    private void addAchievementInstructions(Sheet sheet, CellStyle noteStyle) {
        // 第一行：标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("个人成就信息填写说明");
        titleCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 第二行：说明
        Row instructionRow = sheet.createRow(1);
        Cell instructionCell = instructionRow.createCell(0);
        instructionCell.setCellValue("标*的字段为必填项 | 专家姓名必须与基本信息表中的姓名一致 | 获得日期格式：YYYY-MM-DD");
        instructionCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        // 第三行：注意事项
        Row noteRow = sheet.createRow(2);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("详细描述可填写成就的具体内容、级别等信息 | 请确保数据格式正确");
        noteCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
    }

    /**
     * 创建项目信息sheet
     */
    private void createProjectInfoSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("项目信息");

        // 添加说明
        CellStyle noteStyle = createNoteStyle(workbook);
        addProjectInfoInstructions(sheet, noteStyle);

        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(4); // 从第5行开始（因为项目信息说明较多）

        String[] headers = {
                "项目名称*", "项目赛道*", "项目组别*", "负责人姓名*", "年级", "专业",
                "项目阶段*", "项目状态*", "获奖情况", "指导老师", "项目等级", "创建年份*"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // 各种数据验证
        addDropdownValidation(sheet, validationHelper, 5, 1000, 1,
                new String[]{"高教主赛道", "红旅赛道", "产业赛道"});

        addDropdownValidation(sheet, validationHelper, 5, 1000, 6,
                new String[]{"校赛", "省赛", "国赛"});

        addDropdownValidation(sheet, validationHelper, 5, 1000, 7,
                new String[]{"准备中", "进行中", "已暂停", "已完成", "已终止"});

        addDropdownValidation(sheet, validationHelper, 5, 1000, 8,
                new String[]{"", "一等奖", "二等奖", "三等奖"});

        addDropdownValidation(sheet, validationHelper, 5, 1000, 10,
                new String[]{"重点", "一般", "其他"});

        // 示例数据
        addExampleData(sheet, 5, new String[]{
                "人工智能研究项目", "高教主赛道", "本科生创意组", "李四", "大三", "计算机科学与技术",
                "校赛", "进行中", "一等奖", "王老师", "重点", "2023"
        });
    }

    /**
     * 添加项目信息说明
     */
    private void addProjectInfoInstructions(Sheet sheet, CellStyle noteStyle) {
        // 第一行：标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("项目信息填写说明");
        titleCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

        // 第二行：基本说明
        Row instructionRow1 = sheet.createRow(1);
        Cell instructionCell1 = instructionRow1.createCell(0);
        instructionCell1.setCellValue("标*的字段为必填项 | 项目赛道：高教主赛道/红旅赛道/产业赛道 | 项目阶段：校赛/省赛/国赛 | 项目等级：重点/一般/其他");
        instructionCell1.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 11));

        // 第三行：组别说明标题
        Row groupTitleRow = sheet.createRow(2);
        Cell groupTitleCell = groupTitleRow.createCell(0);
        groupTitleCell.setCellValue("组别对应关系（请根据选择的赛道填写对应的组别）：");
        groupTitleCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 11));

        // 第四行：组别详细说明
        Row groupDetailRow = sheet.createRow(3);
        Cell groupDetailCell = groupDetailRow.createCell(0);
        groupDetailCell.setCellValue("高教主赛道：本科生创意组、本科生创业组、研究生创意组、研究生创业组 | " +
                "红旅赛道：公益组、创意组、创业组 | " +
                "产业赛道：企业命题组、区域特色产业组、成果转化组");
        groupDetailCell.setCellStyle(noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 11));
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建说明行样式
     */
    private CellStyle createNoteStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true); // 自动换行
        return style;
    }

    /**
     * 添加下拉框数据验证
     */
    private void addDropdownValidation(Sheet sheet, DataValidationHelper helper,
                                       int firstRow, int lastRow, int col, String[] options) {
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, col, col);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    /**
     * 添加示例数据
     */
    private void addExampleData(Sheet sheet, int rowNum, String[] data) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < data.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(data[i]);

            // 为示例数据添加浅灰色背景，区分于真实数据
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
    }

}