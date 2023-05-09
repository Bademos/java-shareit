package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByUserId(int userId, Sort sort);

    List<Booking> findAllByUserIdAndStatus(int userId, BookingStatus status, Sort sort);

    List<Booking> findAllByUserIdAndEndBookingBefore(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByUserIdAndEndBookingAfter(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByUserIdAndStartBookingBeforeAndEndBookingAfter(int userId, LocalDateTime dateStart, LocalDateTime dateEnd, Sort sort);

    List<Booking> findAllByItemIdIn(List<Integer> lst, Sort sort);

    List<Booking> findAllByItemIdInAndStatus(List<Integer> lst, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndEndBookingBefore(List<Integer> lst, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdInAndEndBookingAfter(List<Integer> lst, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdInAndStartBookingBeforeAndEndBookingAfter(List<Integer> lst,LocalDateTime dateStr, LocalDateTime dateEnd, Sort sort);

    List<Booking> findAllByItemIdAndStatusAndStartBookingBefore(int itemId, BookingStatus status, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdAndStatusAndStartBookingAfter(int itemId, BookingStatus status, LocalDateTime date, Sort sort);

    List<Booking> findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatus(int itemId, int userId, LocalDateTime now, BookingStatus approved);
}