package com.ehizman.springboot_file_upload.service;

import com.ehizman.springboot_file_upload.exceptions.FIleUploadException;
import com.ehizman.springboot_file_upload.exceptions.FileDownloadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile multipartFile) throws FIleUploadException;

    Object downloadFile(String fileName) throws FileDownloadException;

    String generatePreSignedUploadUrl(String fileName);

    String generatePreSignedDownloadUrl(String fileName) throws FileDownloadException;
}
