package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.user.controller.UserController;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        ItemRequestController.class,
        BookingController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.error("ID not found.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotAvailableException(final NotAvailableException e) {
        log.error("Not availiable exception", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalException(final TimeIntervalException e) {
        log.error("Incorrect conditions", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleVAlidationException(final ValidationException e) {
        log.error("Incorrect validation", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidException(final ConstraintViolationException e) {
        log.error("We in problem", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUnknownStatusException(final Error e) {
        log.error("InternAlServeError", e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}