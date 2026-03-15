package com.expert;

import com.expert.dto.ExpertPageRequestDTO;
import com.expert.dto.ExpertRequestDTO;
import com.expert.dto.ExportRequestDTO;
import com.expert.result.PageResult;
import com.expert.vo.ExpertDetailResponseVO;
import com.expert.vo.ImportVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExpertsService {
    PageResult pageQuery(ExpertPageRequestDTO expertPageRequestDTO);

    ExpertDetailResponseVO expertQueryById(Long expertId);

    String save(ExpertRequestDTO expertRequestDTO);

    void delete(List<Integer> ids);

    void update(ExpertRequestDTO expertRequestDTO);

    /**
     * 导出专家数据
     * @param exportRequestDTO
     * @return
     */
    Workbook exportExperts(ExportRequestDTO exportRequestDTO);

    ImportVO importExperts(MultipartFile file);

    /**
     * 审核通过专家
     * @param id
     */
    void approveExpert(Long id);

    /**
     * 拒绝专家
     * @param id
     * @param reason
     */
    void rejectExpert(Long id, String reason);
}
