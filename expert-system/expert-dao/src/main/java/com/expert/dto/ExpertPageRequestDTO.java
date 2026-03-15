package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExpertPageRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //页码
    private Integer page;

    //每页大小
    private Integer pageSize;

    //搜索关键词
    private String name;

    //状态筛选
    private String status;

    //职称筛选
    private String expertType;

    //权重筛选
    private Integer weight;
}
