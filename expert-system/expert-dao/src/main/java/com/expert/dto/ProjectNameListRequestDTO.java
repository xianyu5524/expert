package com.expert.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectNameListRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //页码
    private Integer page;

    //每页大小
    private Integer pageSize;

    //搜索关键字
    private String keyWord;
}
