package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExportRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;        // 姓名/项目名模糊查询
    private String track;       // 项目赛道
    private String expStatus;   // 专家状态
    private String proStatus;   // 项目状态
    private String stage;       // 项目阶段
    private String projectLevel;// 项目等级
    private String expertType;  // 专家类型

}
