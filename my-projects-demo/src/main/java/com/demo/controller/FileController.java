package com.demo.controller;


import com.demo.dto.FileInfo;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    private static final String folder = "E:\\myworkspace\\security\\my-projects-demo\\src\\main\\java\\com\\demo\\controller";

    @PostMapping
    public FileInfo upload(MultipartFile file) throws IOException {

        System.out.println(file.getName());
        //原始文件名
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());


        File localFile = new File(folder,new Date().getTime()+".txt");
        //写入本地
        file.transferTo(localFile);

        return new FileInfo(localFile.getPath());
    }


    @GetMapping("/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws Exception{

        try(InputStream is = new FileInputStream(new File(folder,id+".txt"));
            OutputStream outputStream = response.getOutputStream();){

            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition","attachment;filename=password.txt");
            IOUtils.copy(is,outputStream);
            outputStream.flush();
        }
    }

}
