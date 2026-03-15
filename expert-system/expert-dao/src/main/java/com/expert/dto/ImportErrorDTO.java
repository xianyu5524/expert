package com.expert.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ImportErrorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer row;
    private String field;
    private String value;
    private String message;
}
