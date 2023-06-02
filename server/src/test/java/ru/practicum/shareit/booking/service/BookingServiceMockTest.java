package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.TimeIntervalException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceMockTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRepositoryDb itemRepository;

    @Mock
    private UserRepositoryDb userRepository;

    @Mock
    private BookingRepository bookingRepository;

    private User userA;
    private User userB;

    private Item itemA;
    private Item itemB;

    private Booking bookingA;
    private BookingDto bookingDtoA;

    private Booking bookingB;
    private BookingDto bookingDtoB;

    static LocalDateTime startDate = LocalDateTime.of(2022, 1, 11, 11, 11, 11);
    static LocalDateTime endDate = LocalDateTime.of(2022, 2, 22, 22, 22, 22);


    @BeforeEach
    public void setUp() {
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
/*
    @BeforeEach
    public void beforeEach() {
        userService.create(userA);
        userService.create(userB);

        itemService.create(itemA);
        itemService.create(itemB);
    }
*/
    @Test
    void createBookingTest() {
        when(bookingRepository.save(any())).thenReturn(bookingA);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemA));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(userB));


        BookingDtoOut booking = bookingService.createBooking(bookingDtoA, userB.getId());

        assertEquals(booking.getId(), bookingA.getId());
        assertEquals(booking.getStart(), bookingA.getStartBooking());
        assertEquals(booking.getEnd(), bookingA.getEndBooking());
        assertEquals(booking.getStatus(), bookingA.getStatus());
    }

    @Test
    void createNotAvailableTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemA));
        assertThrows(NotFoundException.class, () -> {
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

        Booking incorrectBooking = Booking.builder()
                .id(1)
                .endBooking(LocalDateTime.of(2021, 1, 1, 1, 0, 1))
                .startBooking(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                .item(itemInc)
                .user(userB)
                .status(BookingStatus.WAITING)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userB));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemInc));

        itemService.create(itemInc);
        BookingDto incrrectBookingDto = BookingDtoMapper.makeBookingDto(incorrectBooking);
        assertThrows(TimeIntervalException.class, () -> {
            bookingService.createBooking(incrrectBookingDto, incorrectBooking.getUser().getId());
        });
    }


    @Test
    void createBookingByIncrorrectOwnerTest() {
        Item itemInc = Item.builder()
                .id(3)
                .name("test3")
                .description("test test")
                .available(true)
                .owner(userA)
                .build();
        Booking incorrectBooking = Booking.builder()
                .id(1)
                .endBooking(LocalDateTime.of(2021, 1, 1, 1, 10, 1))
                .startBooking(LocalDateTime.of(2021, 1, 1, 1, 1, 1))
                .item(itemInc)
                .user(userA)
                .status(BookingStatus.WAITING)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userB));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemInc));

        itemService.create(itemInc);
        BookingDto incrrectBookingDto = BookingDtoMapper.makeBookingDto(incorrectBooking);
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(incrrectBookingDto, incorrectBooking.getUser().getId());
        });
    }


    @Test
    void updateTest() {
        Booking bookingTmp = Booking.builder()
                .id(1)
                .startBooking(startDate)
                .endBooking(endDate)
                .item(itemA)
                .user(userB)
                .status(BookingStatus.APPROVED)
                .build();
        bookingTmp.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(bookingA));
        when(bookingRepository.save(any())).thenReturn(bookingTmp);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemB));
        BookingDtoOut bookingDtoOut = bookingService.updateBooking(1, true, 1);

        assertEquals(bookingDtoOut.getId(), bookingA.getId());
        assertEquals(bookingDtoOut.getStart(), bookingA.getStartBooking());
        assertEquals(bookingDtoOut.getEnd(), bookingA.getEndBooking());
        assertEquals(bookingDtoOut.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void updateRejectedTest() {
        Booking bookingTmp = Booking.builder()
                .id(1)
                .startBooking(startDate)
                .endBooking(endDate)
                .item(itemA)
                .user(userB)
                .status(BookingStatus.REJECTED)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(bookingA));
        when(bookingRepository.save(any())).thenReturn(bookingTmp);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemB));
        BookingDtoOut bookingDtoOut = bookingService.updateBooking(1, false, 1);

        assertEquals(bookingDtoOut.getId(), bookingA.getId());
        assertEquals(bookingDtoOut.getStart(), bookingA.getStartBooking());
        assertEquals(bookingDtoOut.getEnd(), bookingA.getEndBooking());
        assertEquals(bookingDtoOut.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void updateApprovedBookingTest() {
        bookingA.setStatus(BookingStatus.APPROVED);
        Booking bookingTmp = Booking.builder()
                .id(1)
                .startBooking(startDate)
                .endBooking(endDate)
                .item(itemA)
                .user(userB)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(bookingA));

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemB));
        assertThrows(NotAvailableException.class, () -> {
            bookingService.updateBooking(1, true, 1);
        });
    }

    @Test
    void updateBookingByOtherUserTest() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(1, true, 2);
        });
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(bookingA));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));

        BookingDtoOut booking = bookingService.getBookingById(1, 1);

        assertEquals(booking.getId(), bookingA.getId());
        assertEquals(booking.getStart(), bookingA.getStartBooking());
        assertEquals(booking.getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getBookingByIdWithWrongUserTest() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(1, 99);
        });
    }

    @Test
    void getBookingByIdWithWrongIdTest() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(1, 1);
        });
    }

    @Test
    void getAllTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserId(anyInt(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.ALL, 0, 1);
        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getWaitingTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserIdAndStatus(anyInt(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.WAITING, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getRejectedTest() {
        bookingDtoA.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserIdAndStatus(anyInt(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));
        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.REJECTED, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getFutureTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserIdAndEndBookingAfter(anyInt(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.FUTURE, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getPastTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserIdAndEndBookingBefore(anyInt(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.PAST, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getCurrentTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByUserIdAndStartBookingBeforeAndEndBookingAfter(anyInt(),any(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingByUser(userB.getId(), State.CURRENT, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getWrongUSerTest() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByUser(99, State.CURRENT, 0, 1));
    }

    @Test
    void getBookingsForItemsOfUserWithWrongUSerTest() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingForAllItemsByUser(99, State.CURRENT, 0, 1));
    }

    @Test
    void getBookingsForItemsOfUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdIn(any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.ALL, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getBookingsForItemsOfUserWaitingTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdInAndStatus(any(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.WAITING, 0, 1);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), bookingA.getId());
        assertEquals(booking.get(0).getStart(), bookingA.getStartBooking());
        assertEquals(booking.get(0).getEnd(), bookingA.getEndBooking());
    }

    @Test
    void getBookingsForItemsOfUserRejectedTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdInAndStatus(any(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.REJECTED, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getBookingsForItemsOfUserFutureTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdInAndEndBookingAfter(any(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.FUTURE, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getBookingsForItemsOfUserPastTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdInAndEndBookingBefore(any(),any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));

        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.PAST, 0, 1);

        assertEquals(booking.size(), 1);
    }

    @Test
    void getBookingsForItemsOfUserCurrentTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userA));
        when(bookingRepository.findAllByItemIdInAndStartBookingBeforeAndEndBookingAfter(any(),any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(bookingA)));
        List<BookingDtoOut> booking = bookingService.getBookingForAllItemsByUser(userA.getId(), State.CURRENT, 0, 1);

        assertEquals(booking.size(), 1);
    }
}
