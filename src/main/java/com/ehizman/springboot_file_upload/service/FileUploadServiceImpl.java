package com.ehizman.springboot_file_upload.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.bucketUrl}")
    private String bucketUrl;

    @Override
    public String uploadFile(MultipartFile multipartFile) {

        try {
            //convert multipart file to file
            File file = new File(multipartFile.getOriginalFilename());
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
                fileOutputStream.write(multipartFile.getBytes());
            }
            AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            String fileName = generateFileName(multipartFile);
            String fileUrl = bucketUrl + "/" + bucketName + "/" + fileName;

            //upload file
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/"+ FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            metadata.addUserMetadata("Title", "File Upload - " + fileName);
            metadata.setContentLength(file.length());
            request.setMetadata(metadata);
            s3Client.putObject(request);

            // delete file
            file.delete();

            return fileUrl;

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
