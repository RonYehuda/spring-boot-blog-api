package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError; //A class that represents a single validation error (like "Name can't be blank")
import org.springframework.web.bind.MethodArgumentNotValidException; //The error that Spring throws when @Valid fails
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice //Tells Spring that this class will handle errors from all controllers in the application.
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class) //Tells Spring that this function will handle errors of type MethodArgumentNotValidException
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Sets the HTTP status code to 400 (Bad Request)
    //The function accepts the error as a parameter and returns a ResponseEntity with the answer.
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException exception){
        logger.error("Validation failed for request");
        List<String> errors = new ArrayList<>();
        for (ObjectError error: exception.getBindingResult().getAllErrors()){
            errors.add(error.getDefaultMessage());
        }
        logger.error("Found {} validation errors: {}", errors.size(), errors);
        HashMap<String, List<String>> errorsResponse = new HashMap<>();
            errorsResponse.put("errors",errors);
            return ResponseEntity.badRequest().body(errorsResponse);
    }

}
