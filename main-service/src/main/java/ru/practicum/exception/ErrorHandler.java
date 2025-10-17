package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final BadRequestException e) {
        log.error("Ошибка 400 {}", e.getMessage(), e);
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectRequestTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("Ошибка 400 {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectRequestNotValid(MethodArgumentNotValidException e) {
        log.error("Ошибка 400 {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundRequest(NotFoundException e) {
        log.error("Ошибка 404 {}", e.getMessage());
        return new ErrorResponse("NOT_FOUND", "The required object was not found.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse dateError(ForbiddenException e) {
        log.error("Ошибка 409 {}", e.getMessage());
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse incorrectRequestConflict(ConflictException e) {
        log.error("Ошибка 409 {}", e.getMessage());
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse sqlException(DataIntegrityViolationException e) {
        log.error("Ошибка 400 {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
