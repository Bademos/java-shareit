package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class, GetNotFoundException.class, UpdateNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final Exception e) {
        log.error("ID not found.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final Error e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleDoubleEntityException(final DoubleEntityException e) {
        log.error("Double Entity error:", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}