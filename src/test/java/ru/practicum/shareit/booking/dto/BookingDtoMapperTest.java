package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.item.dto.item.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingDtoMapperTest {
    private User userA;
    private User userB;
    private Item item;
    private final static LocalDateTime startDate =  LocalDateTime.of(2023,1,1,11,11,11);
    private final static LocalDateTime endDate =  LocalDateTime.of(2023,2,2,2,22,22);

    @BeforeEach
   public void setUp() {
        userA = User.builder()
                .id(1)
                .name("userA")
                .email("cu@cu.com")
                .build();
         userB = User.builder()
                .id(1)
                .name("userB")
                .email("ca@ca.com")
                .build();
         item = Item.builder()
                .id(1)
                .name("item")
                .available(Boolean.TRUE)
                .owner(userB)
                .description("booring")
                .build();
    }

    @Test
    void bookingFromBookingDtoTest() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(1)
                .status(BookingStatus.REQUESTED)
                .start(startDate)
                .end(endDate).build();

        Booking booking = BookingDtoMapper.makeBookingFromDto(bookingDto, userA, item);
        assertEquals(booking.getEndBooking(), bookingDto.getEnd());
        assertEquals(booking.getStartBooking(), bookingDto.getStart());
        assertEquals(booking.getUser(),userA);

    }

    @Test
    void bookingDtoFromBookingTest() {
        Booking booking = Booking.builder()
                .id(1)
                .user(userA)
                .item(item)
                .status(BookingStatus.REQUESTED)
                .startBooking(startDate)
                .endBooking(endDate).build();
        BookingDto bookingDto = BookingDtoMapper.makeBookingDto(booking);

        assertEquals(booking.getEndBooking(), bookingDto.getEnd());
        assertEquals(booking.getStartBooking(), bookingDto.getStart());
        assertEquals(booking.getUser(),userA);
    }

    @Test
    void bookingDtoOutFromBookingTest() {
        UserDtoOut userDtoOut = UserDtoMapper.makeUserDtoOutFromUser(userA);
        ItemDtoOut itemDtoOut = ItemDtoMapper.makeItemDtoOutFromItem(item);
        Booking booking = Booking.builder()
                .id(1)
                .user(userA)
                .item(item)
                .status(BookingStatus.REQUESTED)
                .startBooking(startDate)
                .endBooking(endDate).build();
        BookingDtoOut bookingDtoOut = BookingDtoMapper.makeBookingDtoOutFromBooking(booking);
        assertEquals(bookingDtoOut.getId(), bookingDtoOut.getId());
        assertEquals(bookingDtoOut.getItem(), itemDtoOut);
        assertEquals(bookingDtoOut.getBooker(), userDtoOut);
        assertEquals(bookingDtoOut.getStart(), booking.getStartBooking());
    }
}