package com.mynt.JavaExam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    @ResponseBody
    public ResponseEntity<ApiExceptionModel> handleInvalidInputException(InvalidInputException e) {
        ApiExceptionModel error = new ApiExceptionModel(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
