package com.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;


    @RestControllerAdvice
    public class PlayerExceptionHandler {
        @ExceptionHandler

        public ResponseEntity<Exception> httpException(Exception exception) {
            if (exception instanceof NoSuchElementException) {
                return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND); //404

            } else {
                return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST); //400
            }

        }
    }
