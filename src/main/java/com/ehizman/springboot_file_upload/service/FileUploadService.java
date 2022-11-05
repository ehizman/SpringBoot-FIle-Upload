package com.ehizman.springboot_file_upload.service;

import com.ehizman.springboot_file_upload.exceptions.FIleUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile multipartFile) throws FIleUploadException;
}
