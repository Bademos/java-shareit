package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private User user;
    private Item item;
    private LocalDateTime startBooking;
    private LocalDateTime endBooking;
    private BookingStatus status;
}
