package com.ehizman.springboot_file_upload;

import com.ehizman.springboot_file_upload.service.FileService;
import com.ehizman.springboot_file_upload.web.controller.FileUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileOutputStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileUploadController.class)
class SpringBootFileUploadApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void testFileUpload_WhenValidInputThenReturns_200() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/file/upload").file(file)).andExpect(status().isOk());
    }

    @Test
    void testFileUpload_WhenEmptyFileThenReturns_404() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,"".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/file/upload").file(file)).andExpect(status().isNoContent());
    }

    @Test
    void testFileUpload_WhenInValidFileExtensionThenReturns_400() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.xml",
                MediaType.TEXT_PLAIN_VALUE,"Test String".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/file/upload").file(file)).andExpect(status().isBadRequest());
    }

    @Test
    void testFileDownload_WhenBucketIsNotEmptyAndFileIsInBucketThenReturn_200() throws Exception {
        MockMultipartFile multipartFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(multipartFile.getBytes());
        }
        Resource resource = new UrlResource(file.toURI());
        when(fileService.downloadFile(anyString())).thenReturn(resource);
        mockMvc.perform(get("/api/v1/file/download").param("fileName", "1667640772498-The_Woman_King_(2022)_(NetNaija.com).srt"))
                .andExpect(status().isOk());

        file.delete();
    }

}
