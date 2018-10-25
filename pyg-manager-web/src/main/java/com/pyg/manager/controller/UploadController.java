package com.pyg.manager.controller;

import com.pyg.entity.PygResult;
import fastDfsUtils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    //注入图片服务器的地址
    @Value("${FILE_SERVER_URL}")
    private String IMAGE_SERVER_ADDRESS;

    @SuppressWarnings("all")
    //设置成这样的格式兼容性最好
    @RequestMapping(value = "/upload")
    @ResponseBody
    public PygResult uploadPicture(MultipartFile file) {
        try {
            //先得到上传文件的原始全名（包括后缀）
            String filename = file.getOriginalFilename();
            //分割一下得到文件的扩展名
            String extName = filename.substring(filename.lastIndexOf(".") + 1);
            //创建fds客户端
            FastDFSClient fds = new FastDFSClient("classpath:config/fdfs_client.conf");
            //上传文件
            String uploadFileID = fds.uploadFile(file.getBytes(), extName);
            String url = IMAGE_SERVER_ADDRESS + uploadFileID;
            //上传成功
            return new PygResult(true, url);
        } catch (Exception e) {
            //上传失败
            e.printStackTrace();
            return new PygResult(false, "上传失败");
        }
    }
}