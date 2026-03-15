package com.expert.dto;

import com.expert.entity.Expert;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExpertImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Expert> experts;
}
