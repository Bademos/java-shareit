package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.UnknownStatusException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static String description(State state) {
        switch (state) {
            case ALL:
                return "All bookings";
            case CURRENT:
                return "Actual bookings";
            case PAST:
                return "Booking started in the past";
            case WAITING:
                return "The booking is waiting for an approving.";
            case REJECTED:
                return "The booking was rejected.";
            default:
                return "Uncertain state";
        }
    }

    public static State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStatusException("Unknown state: " + state);
        }
    }
}
