package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoOut {
    private int id;
    private UserDtoOut booker;
    private ItemDtoOut item;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
