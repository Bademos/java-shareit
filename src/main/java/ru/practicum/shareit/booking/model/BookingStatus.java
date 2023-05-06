package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    APPROVED,
    REJECTED,
    REQUESTED,
    WAITING,
    CANCELED;

public String description(BookingStatus status) {
    switch (status) {
        case APPROVED:
            return "The Booking approved by owner";
        case CANCELED:
            return "The booking was canceled";
        case REQUESTED:
            return "There is a booking request without approving by owner.";
        default:
            return "Uncertain status.";
    }
}
}
