package ru.practicum.shareit.booking.model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class BookingTest {
    @Test
    void bookingSerializingDateTest() {
        Booking booking = Booking.builder()
                .id(1)
                .startBooking(LocalDateTime.of(2023, 5, 1, 11, 0, 1))
                .endBooking(LocalDateTime.of(2023, 7, 14, 11, 1, 1))
                .item(null)
                .user(null)
                .status(BookingStatus.REQUESTED)
                .build();

        Assertions.assertEquals(booking.getStartBooking().toString(),"2023-05-01T11:00:01");
        Assertions.assertEquals(booking.getEndBooking().toString(),"2023-07-14T11:01:01");
    }
}