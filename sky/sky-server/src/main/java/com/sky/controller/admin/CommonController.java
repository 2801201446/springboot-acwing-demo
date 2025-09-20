package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用控制类，文件上传")
public class CommonController {

    private static String FILE_UPLOAD_PATH = "F:\\upload\\";

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)
    {
        //文件上传
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        File dir = new File(FILE_UPLOAD_PATH);
        //创建文件夹
        if (!dir.exists() || !dir.isDirectory()) {
            boolean created = dir.mkdirs();
            if(created) {
                log.info("创建文件夹成功: {}", FILE_UPLOAD_PATH);
            } else {
                log.warn("创建文件夹失败或已经存在: {}", FILE_UPLOAD_PATH);
            }
        }

        //原始文件命名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名无效");
        }

        // 确保文件路径安全，避免路径遍历攻击
        Path targetLocation = Paths.get(FILE_UPLOAD_PATH).resolve(originalFilename).normalize();
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("文件上传成功: {}", originalFilename);
        } catch (IOException e) {
            log.error("文件上传失败: {}", originalFilename, e);
            return Result.error("文件上传失败");
        }

        // 你可以根据实际情况调整返回的文件访问链接
        String fileUrl = "http://localhost:8080/static/" + originalFilename;
        return Result.success(fileUrl);
    }

}

