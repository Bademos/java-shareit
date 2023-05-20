package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.TimeIntervalException;
import ru.practicum.shareit.exception.UnknownStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    final BookingService bookingService;

    public BookingController(BookingService bookingServiceImpl) {
        this.bookingService = bookingServiceImpl;
    }

    @PostMapping
    BookingDtoOut addBooking(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        log.info("Got request for creating booking" + bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingDtoOut approveBooking(@PathVariable Integer id,
                                 @RequestParam Boolean approved,
                                 @RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        log.info("Got request for approving booking");
        return bookingService.updateBooking(id, approved, userId);
    }

    @GetMapping("/{id}")
    public BookingDtoOut getBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                    @PathVariable int id) {
        log.info("Got request for booking with id:  " + id);
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    List<BookingDtoOut> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "20") @Positive int size) {
        from = from / size;
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new UnknownStatusException("Unknown state: " + state);
        }
        State cState = State.getState(state);
        System.out.println("from" + from + "size" + size);
        return bookingService.getBookingByUser(userId, cState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsOwner(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "20") @Positive int size) {
        if (from < 0) {
            throw new TimeIntervalException("oops");
        }
        from = from / size;
        log.info("Got request for all bookings by User with id:  " + userId);
        State cState = State.getState(state);
        return bookingService.getBookingForAllItemsByUser(userId, cState, from, size);
    }
}