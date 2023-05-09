package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    APPROVED,
    REJECTED,
    REQUESTED,
    WAITING,
    CANCELED;

public static String description(BookingStatus status) {
    switch (status) {
        case APPROVED:
            return "The Booking approved by owner";
        case CANCELED:
            return "The booking was canceled";
        case REQUESTED:
            return "There is a booking request without approving by owner.";
        case WAITING:
            return "The booking is waiting for an approving.";
        case REJECTED:
            return "Unfortunately, the booking was rejected.";
        default:
            return "Uncertain status.";
    }
}
}
