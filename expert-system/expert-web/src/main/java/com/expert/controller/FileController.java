package com.expert.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {
    @GetMapping("download")
    public void downloadFile(@RequestParam String file, HttpServletResponse response) {

        log.info("下载导入的用户账号信息{}",file);
        try {
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + file;
            File downloadFile = new File(filePath);

            if(!downloadFile.exists()){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
            response.setContentLength((int) downloadFile.length());

            // 写入响应流
            try (InputStream inputStream = new FileInputStream(downloadFile);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            // 下载完成后删除临时文件
             downloadFile.delete();
        }catch (IOException e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
