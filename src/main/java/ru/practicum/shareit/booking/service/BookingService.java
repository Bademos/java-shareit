package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    public BookingDtoOut updateBooking(int itemId, boolean isApprove, int userId);

    public BookingDtoOut createBooking(BookingDto bookingDto, int userId);

    public BookingDtoOut getBookingById(int bookingId, int userId);

    public List<BookingDtoOut> getBookingByUser(int userId, State state);

    public List<BookingDtoOut> getBookingForAllItemsByUser(int userId, State state);
}