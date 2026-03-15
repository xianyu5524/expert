package com.expert.vo;

import com.expert.dto.ImportErrorDTO;
import com.expert.dto.ImportExpertSysResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer successCount;
    private Integer failureCount;
    private Integer totalCount;
    private List<ImportErrorDTO> errors;
    private String accountFileUrl; // 账号信息文件下载URL

}
