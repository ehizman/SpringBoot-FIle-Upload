package com.ehizman.springboot_file_upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.ehizman.springboot_file_upload.exceptions.FileDownloadException;
import com.ehizman.springboot_file_upload.exceptions.FileEmptyException;
import com.ehizman.springboot_file_upload.exceptions.SpringBootFileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;


@ControllerAdvice
@Slf4j
public class SpringBootFileUploadExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { FileEmptyException.class})
    protected ResponseEntity<Object> handleFileEmptyException(
            FileEmptyException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NO_CONTENT, request);
    }

    @ExceptionHandler(value
            = { FileDownloadException.class})
    protected ResponseEntity<Object> handleFileDownloadException(
            FileDownloadException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    @ExceptionHandler(value
            = { SpringBootFileUploadException.class})
    protected ResponseEntity<Object> handleConflict(
            SpringBootFileUploadException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Handle exception that occur when the call was transmitted successfully, but Amazon S3 couldn't process
    // it, so it returned an error response.
    @ExceptionHandler(value
            = { AmazonServiceException.class})
    protected ResponseEntity<Object> handleAmazonServiceException(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // Handle exceptions that occur when Amazon S3 couldn't be contacted for a response, or the client
    // couldn't parse the response from Amazon S3.

    @ExceptionHandler(value
            = { SdkClientException.class})
    protected ResponseEntity<Object> handleSdkClientException(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(value
            = {IOException.class, FileNotFoundException.class, MultipartException.class})
    protected ResponseEntity<Object> handleIOException(
            Exception ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = {Exception.class})
    protected ResponseEntity<Object> handleUnExpectedException(
            Exception ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        log.info("Exception ==> ", ex);
        log.info("Fatal exception ===> {}", bodyOfResponse);
        return handleExceptionInternal(ex, "We apologize. Something is not right",
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
