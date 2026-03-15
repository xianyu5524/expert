package com.expert.dto;

import com.expert.entity.Achievement;
import com.expert.entity.Expert;
import com.expert.entity.ExpertProject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpertRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Valid
    @NotNull(message = "专家基本信息不能为空")
    private Expert expert;

    private List<Achievement> achievements;
    private List<ExpertProject> expertProjects;
}
