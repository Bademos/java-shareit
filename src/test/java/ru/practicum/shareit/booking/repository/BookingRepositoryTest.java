package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.List;

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

    Item item;

    User userA;

    User userB;

    Booking booking;

    final static LocalDateTime startDate =  LocalDateTime.of(2023,1,1,11,11,11);
    final static LocalDateTime endDate =  LocalDateTime.of(2023,2,2,2,22,22);

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
        item = Item.builder()
                .id(1)
                .name("item")
                .available(Boolean.TRUE)
                .owner(userB)
                .description("booring")
                .build();
        booking = Booking.builder()
                .id(1)
                .user(userA)
                .item(item)
                .status(BookingStatus.REQUESTED)
                .startBooking(startDate)
                .endBooking(endDate).build();
        userRepository.save(userA);
        userRepository.save(userB);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void findAllTest() {

        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(bookings.get(0).getUser(), userA);
        assertEquals(bookings.get(0).getItem().getOwner(), userB);
    }

    @Test
    void findByIdTest() {
        Booking bookingResp = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(booking.getEndBooking(), bookingResp.getEndBooking());
        assertEquals(booking.getStartBooking(), bookingResp.getStartBooking());
    }


}
