package com.jurgen.task_planner.middelware;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.jurgen.task_planner.models.dtos.ErrorResponse;
import com.jurgen.task_planner.models.dtos.HttpErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpError(HttpErrorException ex) {
        return ResponseEntity
                .status(ex.statusCode)
                .body(new ErrorResponse(
                        ex.statusCode.getReasonPhrase(),
                        ex.message != null ? ex.message : "An error occurred"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                ));
    }
}
