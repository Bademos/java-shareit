package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {
    public static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
    private int id;
    private User user;
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}