package ru.practicum.shareit.exception;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String error) {
        super(error);
    }
}
