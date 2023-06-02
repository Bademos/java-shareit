package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;
import ru.practicum.shareit.util.ConstantsShare;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepositoryDb itemRepository;

    @Autowired
    UserRepositoryDb userRepository;

    Item itemA;

    Item itemB;

    User userA;

    User userB;

    Booking bookingA;
    Booking bookingB;
    Booking bookingC;
    Booking bookingD;
    Booking bookingE;

    final LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 11, 11, 11);
    final LocalDateTime endDate = LocalDateTime.of(2023, 2, 2, 2, 22, 22);
    final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .id(1)
                .name("userA")
                .email("cu@cu.com")
                .build();
        userB = User.builder()
                .id(2)
                .name("userB")
                .email("ca@ca.com")
                .build();
        itemA = Item.builder()
                .id(1)
                .name("itemA")
                .available(Boolean.TRUE)
                .owner(userB)
                .description("booring")
                .build();
        itemB = Item.builder()
                .id(2)
                .name("itemB")
                .available(Boolean.TRUE)
                .owner(userA)
                .description("booring")
                .build();
        bookingA = Booking.builder()
                .id(1)
                .user(userA)
                .item(itemA)
                .status(BookingStatus.REQUESTED)
                .startBooking(startDate)
                .endBooking(endDate).build();
        bookingB = Booking.builder()
                .id(2)
                .user(userB)
                .item(itemB)
                .status(BookingStatus.REQUESTED)
                .startBooking(now.plusDays(2))
                .endBooking(now.plusDays(4)).build();
        bookingC = Booking.builder()
                .startBooking(now.minusMonths(12))
                .endBooking(now.minusMonths(10))
                .user(userA)
                .item(itemB)
                .status(BookingStatus.APPROVED)
                .id(3).build();

        bookingD = Booking.builder()
                .startBooking(now.minusMonths(9))
                .endBooking(now.minusMonths(8))
                .user(userA)
                .item(itemB)
                .status(BookingStatus.APPROVED)
                .id(4).build();

        bookingE = Booking.builder()
                .startBooking(now.minusMonths(7))
                .endBooking(now.minusMonths(6))
                .user(userA)
                .item(itemB)
                .status(BookingStatus.APPROVED)
                .id(5).build();


        userRepository.save(userA);
        userRepository.save(userB);
        itemRepository.save(itemA);
        bookingRepository.save(bookingA);
        itemRepository.save(itemB);
        bookingRepository.save(bookingB);
        bookingRepository.save(bookingC);
        bookingRepository.save(bookingD);
        bookingRepository.save(bookingE);
    }

    @Test
    void findAllTest() {

        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(bookings.get(0).getUser(), userA);
        assertEquals(bookings.get(0).getItem().getOwner(), userB);
    }


    @Test
    void findBookingsLastTest() {

        List<Integer> ids = Collections.singletonList(2);


        List<Booking> lst = bookingRepository.findBookingsLast(ids,
                LocalDateTime.now(),
                userA.getId(), PageRequest.of(0, 1)
        );
        assertEquals(lst.get(0).getId(), bookingE.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingE.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingE.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingE.getStatus());
        assertEquals(lst.get(0).getUser(), bookingE.getUser());
        assertEquals(lst.get(0).getItem(), bookingE.getItem());
        assertEquals(lst.get(0).getId(), 5);
    }

    @Test
    void findBookingsNextTest() {

        bookingC.setStartBooking(LocalDateTime.now().plusMonths(10));
        bookingC.setEndBooking(LocalDateTime.now().plusMonths(12));

        bookingD.setStartBooking(LocalDateTime.now().plusMonths(8));
        bookingD.setEndBooking(LocalDateTime.now().plusMonths(9));

        bookingE.setStartBooking(LocalDateTime.now().plusMonths(6));
        bookingE.setEndBooking(LocalDateTime.now().plusMonths(7));

        bookingRepository.save(bookingC);
        bookingRepository.save(bookingD);
        bookingRepository.save(bookingE);

        List<Integer> ids = Collections.singletonList(2);


        List<Booking> next = bookingRepository.findBookingsNext(ids,
                LocalDateTime.now(),
                userA.getId(), PageRequest.of(0, 1)
        );

        assertEquals(next.get(0).getId(), bookingE.getId());
        assertEquals(next.get(0).getEndBooking(), bookingE.getEndBooking());
        assertEquals(next.get(0).getStartBooking(), bookingE.getStartBooking());
        assertEquals(next.get(0).getStatus(), bookingE.getStatus());
        assertEquals(next.get(0).getUser(), bookingE.getUser());
        assertEquals(next.get(0).getItem(), bookingE.getItem());

        assertEquals(next.size(), 1);
    }

    @Test
    void findAllByUserIdAndStatusTest() {
        Page<Booking> page = bookingRepository.findAllByUserIdAndStatus(2, BookingStatus.REQUESTED, PageRequest.of(0, 2));

        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), 2);
        assertEquals(lst.get(0).getId(), bookingB.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingB.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingB.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingB.getStatus());
        assertEquals(lst.get(0).getUser(), bookingB.getUser());
        assertEquals(lst.get(0).getItem(), bookingB.getItem());
        assertEquals(lst.size(), 1);
    }

    @Test
    void findAllByUserIdAndEndBookingAfterTest() {
        Page<Booking> page = bookingRepository.findAllByUserIdAndEndBookingAfter(2, LocalDateTime.now(), PageRequest.of(0, 2));

        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), 2);
        assertEquals(lst.get(0).getId(), bookingB.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingB.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingB.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingB.getStatus());
        assertEquals(lst.get(0).getUser(), bookingB.getUser());
        assertEquals(lst.get(0).getItem(), bookingB.getItem());
        assertEquals(lst.size(), 1);
    }

    @Test
    void findAllByUserIdAndEndBookingBeforeTest() {
        Page<Booking> page = bookingRepository.findAllByUserIdAndEndBookingBefore(1, LocalDateTime.now(), PageRequest.of(0, 2));

        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), bookingA.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingA.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingA.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingA.getStatus());
        assertEquals(lst.get(0).getUser(), bookingA.getUser());
        assertEquals(lst.get(0).getItem(), bookingA.getItem());
        assertEquals(lst.get(0).getId(), 1);
        assertEquals(lst.size(), 2);
    }

    @Test
    void findAllByUserIdAndStartBookingBeforeAndEndBookingAfterTest() {
        Page<Booking> page = bookingRepository.findAllByUserIdAndStartBookingBeforeAndEndBookingAfter(1, LocalDateTime.now().minusMonths(9), LocalDateTime.now().minusMonths(8).minusDays(1), PageRequest.of(0, 2));
        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), bookingD.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingD.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingD.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingD.getStatus());
        assertEquals(lst.get(0).getUser(), bookingD.getUser());
        assertEquals(lst.get(0).getItem(), bookingD.getItem());
        assertEquals(lst.get(0).getId(), 4);
        assertEquals(lst.size(), 1);
    }

    @Test
    void findAllByItemIdInTest() {
        List<Integer> ids = Arrays.asList(1, 2);
        Page<Booking> page = bookingRepository.findAllByItemIdIn(ids, PageRequest.of(0, 2));
        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), 1);
        assertEquals(lst.size(), 2);
    }

    @Test
    void findAllByItemIdInAndEndBookingBeforeTest() {
        List<Integer> ids = Arrays.asList(1, 2);
        Page<Booking> page = bookingRepository.findAllByItemIdInAndEndBookingBefore(ids, LocalDateTime.now(), PageRequest.of(0, 2));
        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), bookingA.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingA.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingA.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingA.getStatus());
        assertEquals(lst.get(0).getUser(), bookingA.getUser());
        assertEquals(lst.get(0).getItem(), bookingA.getItem());
        assertEquals(lst.get(0).getId(), 1);
        assertEquals(lst.size(), 2);
    }

    @Test
    void findAllByItemIdInAndEndBookingAfterTest() {
        List<Integer> ids = Arrays.asList(1, 2);
        Page<Booking> page = bookingRepository.findAllByItemIdInAndEndBookingAfter(ids, LocalDateTime.now(), PageRequest.of(0, 2));
        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), 2);
        assertEquals(lst.get(0).getId(), bookingB.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingB.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingB.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingB.getStatus());
        assertEquals(lst.get(0).getUser(), bookingB.getUser());
        assertEquals(lst.get(0).getItem(), bookingB.getItem());
        assertEquals(lst.size(), 1);
    }

    @Test
    void findAllByItemIdInAndStartBookingBeforeAndEndBookingAfterTest() {
        List<Integer> ids = Arrays.asList(1, 2);
        Page<Booking> page = bookingRepository.findAllByItemIdInAndStartBookingBeforeAndEndBookingAfter(ids,
                LocalDateTime.now().minusMonths(9),
                LocalDateTime.now().minusMonths(8).minusHours(4),
                PageRequest.of(0, 2));
        List<Booking> lst = page.stream().collect(Collectors.toList());
        assertEquals(lst.get(0).getId(), bookingD.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingD.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingD.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingD.getStatus());
        assertEquals(lst.get(0).getUser(), bookingD.getUser());
        assertEquals(lst.get(0).getItem(), bookingD.getItem());
        assertEquals(lst.get(0).getId(), 4);
        assertEquals(lst.size(), 1);
    }

    @Test
    void findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatusTest() {
        List<Booking> lst = bookingRepository.findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatus(2,
                1,
                LocalDateTime.now(),
                BookingStatus.APPROVED);
        assertEquals(lst.get(0).getId(), bookingC.getId());
        assertEquals(lst.get(0).getEndBooking(), bookingC.getEndBooking());
        assertEquals(lst.get(0).getStartBooking(), bookingC.getStartBooking());
        assertEquals(lst.get(0).getStatus(), bookingC.getStatus());
        assertEquals(lst.get(0).getUser(), bookingC.getUser());
        assertEquals(lst.get(0).getItem(), bookingC.getItem());
        assertEquals(lst.get(0).getId(), 3);
        assertEquals(lst.size(), 3);
    }

    @Test
    void findTopByItemIdAndStatusAndStartBookingBeforeTest() {
        Booking booking = bookingRepository.findTopByItemIdAndStatusAndStartBookingBefore(2,
                BookingStatus.APPROVED,
                LocalDateTime.now(),
                ConstantsShare.sortDesc).orElse(null);
        assert booking != null;
        assertEquals(booking.getStartBooking(), bookingE.getStartBooking());
    }

    @Test
    void findTopByItemIdAndStatusAndStartBookingAfterTest() {
        bookingE.setStartBooking(LocalDateTime.now().plusMonths(120));
        bookingE.setEndBooking(LocalDateTime.now().plusMonths(3600));
        bookingRepository.save(bookingE);

        Booking booking = bookingRepository.findTopByItemIdAndStatusAndStartBookingAfter(2,
                BookingStatus.APPROVED,
                LocalDateTime.now().minusMonths(10),
                ConstantsShare.sortDesc).orElse(null);
        assert booking != null;
        assertEquals(booking.getStartBooking(), bookingE.getStartBooking());
    }
}