package com.ehizman.springboot_file_upload.web.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.ehizman.springboot_file_upload.exceptions.FIleUploadException;
import com.ehizman.springboot_file_upload.exceptions.FileDownloadException;
import com.ehizman.springboot_file_upload.exceptions.FileEmptyException;
import com.ehizman.springboot_file_upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/v1/file")
@Validated
public class FileUploadController {
    private final FileService fileService;


    public FileUploadController(FileService fileUploadService) {
        this.fileService = fileUploadService;
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
                String fileUrl = fileService.uploadFile(multipartFile);
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


    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName")  @NotBlank @NotNull String fileName){
        Object response;
        try {
            response = fileService.downloadFile(fileName);
            if (response != null){
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(response);
            } else {
                APIResponse apiResponse = APIResponse.builder()
                        .message("File could not be downloaded")
                        .isSuccessful(false)
                        .statusCode(400)
                        .build();
                return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
            }
        } catch (FileDownloadException e) {
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/generate-download-url")
    public ResponseEntity<?> getPresignedDownloadURL(@RequestParam("fileName") @NotBlank @NotNull String fileName){
        try {
            String preSignedUrl = fileService.generatePreSignedDownloadUrl(fileName);
            APIResponse apiResponse = APIResponse.builder()
                    .message("Successfully generated presigned URL for file sharing")
                    .data(preSignedUrl)
                    .expiresIn("10 minutes")
                    .isSuccessful(true)
                    .statusCode(200)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        catch (FileDownloadException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        }
        catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/generate-upload-url")
    public ResponseEntity<?> getPresignedUploadURL(@RequestParam("fileName") @NotBlank @NotNull String fileName){
        try {
            String preSignedUrl = fileService.generatePreSignedUploadUrl(fileName);
            APIResponse apiResponse = APIResponse.builder()
                    .message("Successfully generated presigned upload URL")
                    .data(preSignedUrl)
                    .expiresIn("5 minutes")
                    .isSuccessful(true)
                    .statusCode(200)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            APIResponse apiResponse = APIResponse.builder()
                    .message(e.getMessage())
                    .isSuccessful(false)
                    .statusCode(400)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("fileName") @NotBlank @NotNull String fileName){
        File file = Paths.get(fileName).toFile();
        if (file.exists()){
            file.delete();
            return new ResponseEntity<>("file deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("file do not exist", HttpStatus.NOT_FOUND);
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
