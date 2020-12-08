package com.housekeeping.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.housekeeping.im.common.utils.ChatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequestMapping("api")
public class UploadController {

    @Value("${web.upload-path}")
    private String uploadPath;

    /**
     * 上传接口
     *
     * @param file    文件
     * @param request 请求
     * @return json
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            String host = ChatUtils.getHost(request);
            String fileName = UUID.randomUUID() + "." + file.getOriginalFilename().substring(Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".") + 1);
            File targetFile = new File(uploadPath);
            if (!targetFile.exists()) {
                if (!targetFile.mkdirs()) {
                    json.put("msg", "error");
                    return json.toJSONString();
                }
            }
            File tempFile = new File(uploadPath, fileName);
            file.transferTo(tempFile);
            json.put("msg", "success");
            json.put("filePath", host + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            json.put("msg", "error");
            return json.toJSONString();
        }
        return json.toJSONString();
    }
}
