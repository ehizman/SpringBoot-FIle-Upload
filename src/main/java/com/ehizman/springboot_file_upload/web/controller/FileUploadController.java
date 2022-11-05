package com.ehizman.springboot_file_upload.web.controller;

import com.ehizman.springboot_file_upload.exceptions.FIleUploadException;
import com.ehizman.springboot_file_upload.exceptions.FileEmptyException;
import com.ehizman.springboot_file_upload.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/v1/file")
public class FileUploadController {
    private final FileUploadService fileUploadService;


    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile){

        try {
            if (multipartFile.isEmpty()){
                throw new FileEmptyException("File is empty. Cannot save an empty file");
            }
            boolean isValidFile = isValidFile(multipartFile);
            List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList("pdf", "txt", "epub", "csv", "png", "jpg", "jpeg", "srt"));

            if (isValidFile && allowedFileExtensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))){
                String fileUrl = fileUploadService.uploadFile(multipartFile);
                APIResponse apiResponse = APIResponse.builder()
                            .message("file uploaded successfully. File can be downloaded at " + fileUrl)
                            .isSuccessful(true)
                            .statusCode(200)
                            .build();
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                APIResponse apiResponse = APIResponse.builder()
                        .message("Invalid File. File extension or File name is not supported")
                        .isSuccessful(false)
                        .statusCode(400)
                        .build();
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
        }
        catch (FIleUploadException exception){
            log.info("Could not  upload file");
            exception.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message("Invalid File.\nFile extension or File name is not supported")
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        catch (FileEmptyException exception){
            log.error("File is Empty");
            APIResponse apiResponse = APIResponse.builder()
                    .message("File is empty")
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
    }

    private boolean isValidFile(MultipartFile multipartFile){
        log.info("Empty Status ==> {}", multipartFile.isEmpty());
        if (Objects.isNull(multipartFile.getOriginalFilename())){
            return false;
        }
        return !multipartFile.getOriginalFilename().trim().equals("");
    }
}
