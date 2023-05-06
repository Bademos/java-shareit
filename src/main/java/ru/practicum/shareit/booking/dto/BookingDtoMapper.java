package ru.practicum.shareit.booking.dto;


import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;

public class BookingDtoMapper {
    public static BookingDto makeBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .userId(booking.getUser().getId())
                .start(booking.getStartBooking())
                .end(booking.getEndBooking())
                .status(booking.getStatus())
                .build();
    }

    public static Booking makeBookingFromDto(BookingDto bookingDto, User user, Item item) {
        return new Booking(bookingDto.getId(), user, item,
                bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getStatus());
    }

    public static BookingDtoOut makeBookingDtoOutFromBooking(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .booker(UserDtoOut.builder().id(booking.getUser().getId()).build())
                .item(ItemDtoOut.builder().id(booking.getItem().getId()).name(booking.getItem().getName()).build())
                .status(booking.getStatus())
                .end(booking.getEndBooking())
                .start(booking.getStartBooking())
                .build();
    }

    public static Booking makeBookingFromBookingDtoOut(BookingDtoOut booking, User user, Item item) {
        return Booking.builder()
                .id(booking.getId())
                .user(user)
                .item(item)
                .status(booking.getStatus())
                .endBooking(booking.getEnd())
                .startBooking(booking.getStart())
                .build();
    }
}