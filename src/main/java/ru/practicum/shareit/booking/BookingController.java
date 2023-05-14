package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnknownStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingController {
    final BookingService bookingService;

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
    List<BookingDtoOut> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new UnknownStatusException("Unknown state: " + state);
        }
        State cState = State.getState(state);
        return bookingService.getBookingByUser(userId, cState);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsOwner(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Got request for all bookings by User with id:  " + userId);
        State cState = State.getState(state);
        return bookingService.getBookingForAllItemsByUser(userId, cState);
    }
}