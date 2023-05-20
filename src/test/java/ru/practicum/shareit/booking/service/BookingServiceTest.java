package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.TimeIntervalException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private static User userA;
    private static User userB;

    private static Item itemA;
    private static Item itemB;

    private static Booking bookingA;
    private static BookingDto bookingDtoA;

    private static Booking bookingB;
    private static BookingDto bookingDtoB;

    static LocalDateTime startDate = LocalDateTime.of(2022, 1, 11, 11, 11, 11);
    static LocalDateTime endDate = LocalDateTime.of(2022, 2, 22, 22, 22, 22);


    @BeforeAll
    public static void beforeAll() {
        userA = User.builder()
                .id(1)
                .name("userA")
                .email("cu@cu.com")
                .build();

        userB = User.builder()
                .id(2)
                .name("userB")
                .email("co@co.com")
                .build();
        itemA = Item.builder()
                .id(1)
                .name("testA")
                .description("boring")
                .available(true)
                .owner(userA)
                .build();

        itemB = Item.builder()
                .id(2)
                .name("test")
                .description("cooling")
                .available(false)
                .owner(userA)
                .build();


        bookingA = Booking.builder()
                .id(1)
                .startBooking(startDate)
                .endBooking(endDate)
                .item(itemA)
                .user(userB)
                .status(BookingStatus.WAITING)
                .build();


        bookingDtoA = BookingDtoMapper.makeBookingDto(bookingA);


        bookingB = Booking.builder()
                .id(2)
                .startBooking(LocalDateTime.of(2020, 11, 11, 1, 1, 1))
                .endBooking(LocalDateTime.of(2020, 11, 11, 11, 1, 1))
                .item(itemB)
                .user(userB)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoB = BookingDtoMapper.makeBookingDto(bookingB);

    }

    @BeforeEach
    public void beforeEach() {
        userService.create(userA);
        userService.create(userB);

        itemService.create(itemA);
        itemService.create(itemB);
    }

    @Test
    void createBookingTest() {
        BookingDtoOut booking = bookingService.createBooking(bookingDtoA, userB.getId());

        assertEquals(booking.getId(), bookingA.getId());
        assertEquals(booking.getStart(), bookingA.getStartBooking());
        assertEquals(booking.getEnd(), bookingA.getEndBooking());
        assertEquals(booking.getStatus(), bookingA.getStatus());
    }

    @Test
    void createNotAvailableTest() {
        assertThrows(NotAvailableException.class, () -> {
            bookingService.createBooking(bookingDtoB, userB.getId());
        });
    }


    @Test
    void createIncorrectTimingTest() {
        Item itemInc = Item.builder()
                .id(3)
                .name("test3")
                .description("test test")
                .available(true)
                .owner(userA)
                .build();
        itemService.create(itemInc);
        Booking incorrectBooking = Booking.builder()
                .id(1)
                .endBooking(LocalDateTime.of(2021, 1, 1, 1, 0, 1))
                .startBooking(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                .item(itemInc)
                .user(userB)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto incrrectBookingDto = BookingDtoMapper.makeBookingDto(incorrectBooking);


        assertThrows(TimeIntervalException.class, () -> {
            bookingService.createBooking(incrrectBookingDto, incorrectBooking.getUser().getId());
        });
    }

    @Test
    void updateTest() {
        bookingService.createBooking(bookingDtoA, bookingA.getUser().getId());
        BookingDtoOut bookingDtoOut = bookingService.updateBooking(1, true, 1);

        assertEquals(bookingDtoOut.getId(), bookingA.getId());
        assertEquals(bookingDtoOut.getStart(), bookingA.getStartBooking());
        assertEquals(bookingDtoOut.getEnd(), bookingA.getEndBooking());
        assertEquals(bookingDtoOut.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void updateApprovedBookingTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        bookingService.updateBooking(1, true, 1);
        assertThrows(NotAvailableException.class, () -> {
            bookingService.updateBooking(1, true, 1);
        });
    }


    @Test
    void updateBookingByOtherUserTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(1, true, 2);
        });
    }

    @Test
    void getBookingByIdTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        BookingDtoOut booking = bookingService.getBookingById(1, 1);

        assertEquals(booking.getId(), bookingA.getId());
        assertEquals(booking.getStart(), bookingA.getStartBooking());
        assertEquals(booking.getEnd(), bookingA.getEndBooking());
    }


    @Test
    void getWaitingTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.WAITING, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getRejectedTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.REJECTED, 0, 1);

        assertEquals(booking.size(), 0);
    }

    @Test
    void getFutureTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.FUTURE, 0, 1);

        assertEquals(booking.size(), 0);
    }

    @Test
    void getPastTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.PAST, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getCurrentTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.CURRENT, 0, 1);

        assertEquals(booking.size(), 0);
    }

    @Test
    void getBookingsForItemsOfUserTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.ALL, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getBookingsForItemsOfUserWaitingTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.WAITING, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getBookingsForItemsOfUserRejectedTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.REJECTED, 0, 1);

        assertEquals(booking.size(), 0);
    }

    @Test
    void getBookingsForItemsOfUserFutureTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.FUTURE, 0, 1);

        assertEquals(booking.size(), 0);
    }

    @Test
    void getBookingsForItemsOfUserPastTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.PAST, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getBookingsForItemsOfUserCurrentTest() {
        bookingService.createBooking(bookingDtoA, userB.getId());
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.CURRENT, 0, 1);

        assertEquals(booking.size(), 0);
    }
}
