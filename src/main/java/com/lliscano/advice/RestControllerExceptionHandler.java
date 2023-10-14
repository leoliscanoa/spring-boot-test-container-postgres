package com.lliscano.advice;
import com.lliscano.dto.ResponseDTO;
import com.lliscano.exception.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UsersControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationExceptionHandler(final ConstraintViolationException exception) {
        log.error("ConstraintViolationException", exception);
        List<Map<String, String>> errors = new ArrayList<>();
        exception.getConstraintViolations()
                .forEach(message -> {
                    HashMap<String, String> error = new HashMap<>();
                    error.put(message.getPropertyPath().toString(), message.getMessage());
                    errors.add(error);
                });
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .message("constraintViolationExceptionHandler::InvalidParameters")
                        .data(errors)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ){
        log.error("MethodArgumentNotValidException::InvalidParameters", ex);
        List<Map<String, String>> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors()
                .forEach(message -> {
                    HashMap<String, String> error = new HashMap<>();
                    error.put(((FieldError) message).getField(),message.getDefaultMessage());
                    errors.add(error);
                });
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .message("MethodArgumentNotValidException::InvalidParameters")
                        .data(errors)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> recordNotFoundExceptionHandler(final Exception exception) {
        log.error("RecordNotFoundException", exception);
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .message(exception.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> genericExceptionHandler(final Exception exception) {
        log.error("GenericExceptionHandler", exception);
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .message("Internal server error")
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
