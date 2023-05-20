package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@JsonTest
public class BookingTest {
    @Autowired
    private JacksonTester<Booking> jacksonTester;

    @Test
    void bookingSerializingDateTest() throws IOException {
        Booking booking = Booking.builder()
                .id(1)
                .startBooking(LocalDateTime.of(2023, 5, 1, 11, 0, 1))
                .endBooking(LocalDateTime.of(2023, 7, 14, 11, 1, 1))
                .status(BookingStatus.REQUESTED)
                .build();
        JsonContent<Booking> json = jacksonTester.write(booking);

        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-05-01T11:00:01");
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-07-14T11:01:01");
    }

}
