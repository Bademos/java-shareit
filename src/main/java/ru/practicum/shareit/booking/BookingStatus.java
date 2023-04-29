package ru.practicum.shareit.booking;

public enum BookingStatus {
APPROVED,
REQUESTED,
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
