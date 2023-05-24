package ru.practicum.shareit.exception;

public class UnknownStatusException extends Error {
    public UnknownStatusException(String error) {
        super(error);
    }
}
