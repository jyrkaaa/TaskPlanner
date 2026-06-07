package com.jurgen.task_planner.models.dtos;

import java.util.Optional;

import org.springframework.http.HttpStatus;

public class HttpErrorException extends Exception {
    public HttpStatus statusCode;
    public String message;

    public HttpErrorException(Optional<HttpStatus> statusCode) {
        this.statusCode = statusCode.orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        this.message = "";
    }

    public HttpErrorException(String message, HttpStatus statusCode) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpErrorException(String message) {
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
    }
}
