package com.expert.utils;

import com.expert.dto.ImportErrorDTO;
import com.expert.dto.ImportExpertSysResponseDTO;
import com.expert.entity.Expert;
import com.expert.entity.Project;
import com.expert.mapper.ExpertsMapper;
import com.expert.mapper.ProjectsMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImportUtils {

    @Autowired
    ExpertsMapper expertsMapper;

    @Autowired
    ProjectsMapper projectsMapper;

    /**
     * 解析专家基本信息sheet
     */
    public List<Expert> parseExpertSheet(Sheet sheet, List<ImportErrorDTO> errors) {
        List<Expert> experts = new ArrayList<>();

        // 从第6行开始读取（跳过3行说明信息，1行表头，1行示例）
        for (int i = 5; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Expert expert = new Expert();

                // 读取单元格数据
                expert.setName(getCellStringValue(row.getCell(0)));
                expert.setGender(convertGender(getCellStringValue(row.getCell(1))));
                expert.setBirthDate(parseDate(getCellStringValue(row.getCell(2))));
                expert.setTitle(getCellStringValue(row.getCell(3)));
                expert.setUnit(getCellStringValue(row.getCell(4)));
                expert.setDepartment(getCellStringValue(row.getCell(5)));
                expert.setField(getCellStringValue(row.getCell(6)));
                expert.setExpertType(convertExpertType(getCellStringValue(row.getCell(7))));
                expert.setWeight(parseWeight(getCellStringValue(row.getCell(8))));
                expert.setBankAccount(getCellStringValue(row.getCell(9)));
                expert.setBankName(getCellStringValue(row.getCell(10)));
                expert.setPhone(getCellStringValue(row.getCell(11)));
                expert.setEmail(getCellStringValue(row.getCell(12)));
                expert.setIntroduction(getCellStringValue(row.getCell(13)));

                // 跳过空行
                if (isBlankRow(expert)) {
                    continue;
                }

                experts.add(expert);

            } catch (Exception e) {
                errors.add(ImportErrorDTO.builder()
                        .row(i + 1)
                        .field("解析数据")
                        .value("")
                        .message("数据解析失败: " + e.getMessage())
                        .build());
            }
        }

        return experts;
    }

    /**
     * 验证专家数据
     */
    public void validateExpertData(Expert expert) {
        if (expert.getName() == null || expert.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (expert.getPhone() == null || expert.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (expert.getEmail() == null || expert.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }

        // 验证手机号格式
        if (!expert.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }

        // 验证邮箱格式
        if (!expert.getEmail().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            throw new RuntimeException("邮箱格式不正确");
        }

        // 检查手机号是否已存在
        if (expertsMapper.existsByPhone(expert.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        // 检查邮箱是否已存在
        if (expertsMapper.existsByEmail(expert.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
    }

    /**
     * 生成账号信息Excel文件
     */
    public String generateAccountInfoExcel(List<ImportExpertSysResponseDTO> userData) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("专家账号信息");

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"姓名", "用户名", "初始密码", "邮箱", "手机号", "单位", "部门"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 填充数据
            int rowNum = 1;
            for (ImportExpertSysResponseDTO user : userData) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(user.getName());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getPassword());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getPhone());
                row.createCell(5).setCellValue(user.getUnit());
                row.createCell(6).setCellValue(user.getDepartment());
            }

            // 保存文件到临时目录
            String fileName = "专家账号信息_" + System.currentTimeMillis() + ".xlsx";
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            // 返回文件访问URL（根据您的文件服务配置调整）
            return "/api/files/download?file=" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("生成账号信息Excel失败", e);
        }
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
        return style;
    }

    // 辅助方法
    private  String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private  String convertGender(String gender) {
        if ("男".equals(gender)) return "MALE";
        if ("女".equals(gender)) return "FEMALE";
        return gender;
    }

    private  String convertExpertType(String expertType) {
        if ("教育专家".equals(expertType)) return "EDUCATION";
        if ("企业专家".equals(expertType)) return "ENTERPRISE";
        return expertType;
    }

    private  Integer parseWeight(String weightStr) {
        if (weightStr == null || weightStr.trim().isEmpty()) return 3;
        try {
            return Integer.parseInt(weightStr);
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    private  LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private  boolean isBlankRow(Expert expert) {
        return (expert.getName() == null || expert.getName().trim().isEmpty()) &&
                (expert.getPhone() == null || expert.getPhone().trim().isEmpty()) &&
                (expert.getEmail() == null || expert.getEmail().trim().isEmpty());
    }

    public List<Project> parseProjectSheet(Sheet sheet, List<ImportErrorDTO> errors) {
        List<Project> projects = new ArrayList<>();

        // 从第7行开始读取（跳过4行说明信息，1行表头，1行示例）
        for (int i = 6; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Project project = new Project();

                project.setName(getCellStringValue(row.getCell(0)));
                project.setTrack(convertTrack(getCellStringValue(row.getCell(1))));
                project.setCategory(convertCategory(getCellStringValue(row.getCell(2))));
                project.setLeaderName(getCellStringValue(row.getCell(3)));
                project.setGrade(getCellStringValue(row.getCell(4)));
                project.setMajor(getCellStringValue(row.getCell(5)));
                project.setStage(convertStage(getCellStringValue(row.getCell(6))));
                project.setStatus(convertProjectStatus(getCellStringValue(row.getCell(7))));
                project.setAward(convertAward(getCellStringValue(row.getCell(8))));
                project.setAdvisor(getCellStringValue(row.getCell(9)));
                project.setProjectLevel(convertProjectLevel(getCellStringValue(row.getCell(10))));
                project.setCreatedYear(getCellStringValue(row.getCell(11)));

                if (isBlankProjectRow(project)) {
                    continue;
                }

                projects.add(project);

            } catch (Exception e) {
                errors.add(ImportErrorDTO.builder()
                        .row(i + 1)
                        .field("解析数据")
                        .value("")
                        .message("数据解析失败: " + e.getMessage())
                        .build());
            }
        }

        return projects;
    }

    public void validateProjectData(Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new RuntimeException("项目名称不能为空");
        }
        if (project.getLeaderName() == null || project.getLeaderName().trim().isEmpty()) {
            throw new RuntimeException("负责人姓名不能为空");
        }
        if (project.getCreatedYear() == null || project.getCreatedYear().trim().isEmpty()) {
            throw new RuntimeException("创建年份不能为空");
        }

        // 检查项目名称是否已存在
        if (projectsMapper.existsByName(project.getName())) {
            throw new RuntimeException("项目名称已存在");
        }
    }


    // 转换方法
    private String convertTrack(String track) {
        if ("高教主赛道".equals(track)) return "MAIN_TRACK";
        if ("红旅赛道".equals(track)) return "RED_TOURISM_TRACK";
        if ("产业赛道".equals(track)) return "INDUSTRY_TRACK";
        return track;
    }

    private String convertStage(String stage) {
        if ("校赛".equals(stage)) return "SCHOOL";
        if ("省赛".equals(stage)) return "PROVINCIAL";
        if ("国赛".equals(stage)) return "NATIONAL";
        return stage;
    }

    private String convertProjectStatus(String status) {
        switch (status) {
            case "准备中": return "PREPARING";
            case "进行中": return "ONGOING";
            case "已暂停": return "SUSPENDED";
            case "已完成": return "COMPLETED";
            case "已终止": return "TERMINATED";
            default: return status;
        }
    }

    private String convertAward(String award) {
        if ("一等奖".equals(award)) return "FIRST_PRIZE";
        if ("二等奖".equals(award)) return "SECOND_PRIZE";
        if ("三等奖".equals(award)) return "THIRD_PRIZE";
        return award;
    }

    private String convertProjectLevel(String level) {
        if ("重点".equals(level)) return "KEY";
        if ("一般".equals(level)) return "GENERAL";
        if ("其他".equals(level)) return "OTHER";
        return level;
    }

    private String convertCategory(String category) {
        // 根据实际业务逻辑转换项目类别
        return category;
    }

    private boolean isBlankProjectRow(Project project) {
        return (project.getName() == null || project.getName().trim().isEmpty()) &&
                (project.getLeaderName() == null || project.getLeaderName().trim().isEmpty());
    }

}
