package com.ehizman.springboot_file_upload;

import com.ehizman.springboot_file_upload.service.FileUploadService;
import com.ehizman.springboot_file_upload.web.controller.FileUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileUploadController.class)
class SpringBootFileUploadApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    void whenValidInputThenReturns_200() throws Exception {
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
    void whenEmptyFileThenReturns_404() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,"".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/file/upload").file(file)).andExpect(status().isNotFound());
    }

    @Test
    void whenInValidFileExtensionThenReturns_400() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.xml",
                MediaType.TEXT_PLAIN_VALUE,"Test String".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/file/upload").file(file)).andExpect(status().isBadRequest());
    }

}
