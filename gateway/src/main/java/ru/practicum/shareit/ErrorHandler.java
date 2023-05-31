package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;


import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        ItemRequestController.class,
        BookingController.class})

public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotAvailableException(final IllegalArgumentException e) {
        log.error("Not availiable exception", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUnknownException(final Throwable e) {
        log.error("InternAlServeError", e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidException(final MethodArgumentNotValidException e) {
        log.error("Entity dos not pass inner validation", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}