package com.ehizman.springboot_file_upload.service;

import com.ehizman.springboot_file_upload.exceptions.FIleUploadException;
import com.ehizman.springboot_file_upload.exceptions.FileDownloadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile multipartFile) throws FIleUploadException, IOException;

    Object downloadFile(String fileName) throws FileDownloadException, IOException;

    boolean delete(String fileName);
}
