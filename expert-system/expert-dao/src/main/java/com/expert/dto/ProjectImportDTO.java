package com.expert.dto;

import com.expert.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProjectImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Project> projects;
}
