package com.expert;


import javax.servlet.http.HttpServletResponse;

public interface TemplateService {
    void generateExpertTemplate(HttpServletResponse response);
    void generateProjectTemplate(HttpServletResponse response);

}
