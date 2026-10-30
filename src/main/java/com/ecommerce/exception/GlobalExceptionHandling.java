package com.ecommerce.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> notFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message = "+ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> conflict(ConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("message = "+ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> unAuthorized(UnauthorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("message = "+ex.getMessage());
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badException(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message = "+ex.getMessage());
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> insufficientStockException(InsufficientStockException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("message = "+ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> ForbiddenException (ForbiddenException  ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("message = "+ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgument(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message = "+ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> dataIntegrity(DataIntegrityViolationException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("message = Duplicate or invalid data");
    }

}
