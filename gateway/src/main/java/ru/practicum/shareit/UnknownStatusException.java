package ru.practicum.shareit;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String error) {
        super(error);
    }
}
