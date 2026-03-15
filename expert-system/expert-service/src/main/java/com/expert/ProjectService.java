package com.expert;

import com.expert.dto.ExportRequestDTO;
import com.expert.dto.ProjectNameListRequestDTO;
import com.expert.dto.ProjectPageRequestDTO;
import com.expert.entity.Project;
import com.expert.result.PageResult;
import com.expert.vo.ImportVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ProjectService {
    PageResult getNameList(ProjectNameListRequestDTO projectNameListRequestDTO);

    void saveProject(Project project);

    PageResult getPageList(ProjectPageRequestDTO projectPageRequestDTO);

    Project getProjectById(Long id);

    void updateProject(Project project);

    void deleteProject(List<Integer> ids);

    Workbook exportProjects(ExportRequestDTO exportRequestDTO);

    ImportVO importProjects(MultipartFile file);
}
