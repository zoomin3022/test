package com.example.test.global.exception;

import com.example.test.global.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        log.error("--------------------------------");
        log.error("StackTrace = {} ", (Object) e.getStackTrace());
        log.error("HttpStatus = {} ", e.getCustomExceptionType().getHttpStatus());
        log.error("Code = {} ", e.getCustomExceptionType().getCode());
        log.error("Message = {} ", e.getCustomExceptionType().getMessage());
        log.error("--------------------------------");

        final ErrorResponse errorResponse = ErrorResponse.of(e.getCustomExceptionType().getCode(), e.getCustomExceptionType().getMessage());

        return ResponseEntity.status(e.getCustomExceptionType().getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getField());
            builder.append("은/는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        String errorMsg = builder.toString();

        log.error("--------------------------------");
        log.error("StackTrace = {} ", (Object) e.getStackTrace());
        log.error("HttpStatus = {} ", httpStatus);
        log.error("Message = {} ", errorMsg);
        log.error("--------------------------------");

        return ResponseEntity.status(httpStatus)
                .body(errorMsg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        String errorMsg = e.getParameterName() + "의 값을 넣어주세요.";

        log.error("--------------------------------");
        log.error("StackTrace = {} ", (Object) e.getStackTrace());
        log.error("HttpStatus = {} ", httpStatus);
        log.error("Message = {} ", errorMsg);
        log.error("--------------------------------");

        return ResponseEntity.status(httpStatus)
                .body(errorMsg);
    }
}
