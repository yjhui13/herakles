package com.lcw.herakles.platform.system.files.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lcw.herakles.platform.common.constant.ApplicationConstant;
import com.lcw.herakles.platform.system.files.consts.FileConsts;

/**
 * 文件模版.
 * 
 * @author chenwulou
 *
 */
@Controller
@RequestMapping(value = "system/file/template")
public class FileTemplateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTemplateController.class);

    /**
     * 文件模版下载.
     * 
     * 用法:Controller从FileTemplateConsts获取fileName,showFileName,suffixes,filePath
     * ,放到model,前台获取Model请求system/file/template/download?xxx
     * 
     * @param fileName 模版文件名
     * @param showFileName 显示文件名
     * @param suffixes 文件后缀
     * @param filePath 文件路径
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "download")
    public void getFile(@RequestParam(value = "fileName") String fileName,
            @RequestParam(value = "showFileName") String showFileName,
            @RequestParam(value = "suffixes") String suffixes,
            @RequestParam(value = "filePath") String filePath, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String recommendedName = URLEncoder.encode(showFileName, ApplicationConstant.UTF_8);

        StringBuilder realFilePath = new StringBuilder();
        realFilePath.append(filePath);
        // realFilePath.append(File.separator);
        realFilePath.append(fileName);
        realFilePath.append(suffixes);

        StringBuilder header = new StringBuilder();
        header.append("attachment; filename=");
        header.append(recommendedName);
        header.append(suffixes);

        response = this.handleResponseContentType(response, suffixes);
        response.setCharacterEncoding(ApplicationConstant.UTF_8);
        response.setHeader("Content-Disposition", header.toString());
        response.resetBuffer();

        InputStream is = FileTemplateController.class.getClassLoader()
                .getResourceAsStream(realFilePath.toString());
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buffer = new byte[8192];
        int length = 0;
        while ((length = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, length);
        }

        bos.flush();
        bos.close();
        bis.close();
        is.close();
    }

    private HttpServletResponse handleResponseContentType(HttpServletResponse response,
            String suffixes) {
        if (StringUtils.equals(FileConsts.XLSX, suffixes) || StringUtils.equals(FileConsts.XLS, suffixes)) {
            response.setContentType("application/vnd.ms-excel");
        } else if (StringUtils.equals(FileConsts.DOCX, suffixes) || StringUtils.equals(FileConsts.DOC, suffixes)) {
            response.setContentType("application/msword");
        } else if (StringUtils.equals(FileConsts.PDF, suffixes)) {
            response.setContentType("application/pdf");
        } else {
            LOGGER.info("out of file type : {}", suffixes);
            response.setContentType("application/octet-stream");
        }
        return response;
    }

}
