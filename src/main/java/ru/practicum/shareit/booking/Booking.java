package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private int userId;
    private int itemId;
    private LocalDateTime startBooking;
    private LocalDateTime endBooking;
}
