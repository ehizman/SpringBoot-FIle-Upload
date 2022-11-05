package com.ehizman.springboot_file_upload.web.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class APIResponse {
    private String message;
    private boolean isSuccessful;
    private int statusCode;
}

