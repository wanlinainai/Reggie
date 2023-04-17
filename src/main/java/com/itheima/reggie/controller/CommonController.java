package com.itheima.reggie.controller;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 15:42
 */

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 进行文件的上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 下载方法
     *
     * @return
     */
    @PostMapping("/upload")
    //参数的名字不能随便写，必须和前端穿过来的name保持一致
    public R<String> upload(MultipartFile file) {
        //得到的这个file只是一个临时文件，很快便会消失，我们需要给它转存到指定位置
        log.info(file.toString());

        //获得原始文件名
        String originalFilename = file.getOriginalFilename();

        //获取后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为了防止发生名字相同覆盖图片问题，我们使用UUID来为每个图片创建新的名字
        String fileName = UUID.randomUUID().toString() + suffix;

        //判断basePath路径是否是存在的，如果存在，直接加上。不存在，创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            //如果将转存的位置固定死之后维护救护显得很麻烦，所以我们需要动态定义
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回到浏览器中，可以在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
