package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByUserIdOrderByStartBookingDesc(int userId);

    List<Booking> findAllByUserIdAndStatusOrderByStartBookingDesc(int userId, BookingStatus status);

    List<Booking> findAllByUserIdAndEndBookingBeforeOrderByStartBookingDesc(int userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndEndBookingAfterOrderByStartBookingDesc(int userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(int userId, LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Booking> findAllByItemIdInOrderByStartBookingDesc(List<Integer> lst);

    List<Booking> findAllByItemIdInAndStatusOrderByStartBookingDesc(List<Integer> lst, BookingStatus status);

    List<Booking> findAllByItemIdInAndEndBookingBeforeOrderByStartBookingDesc(List<Integer> lst, LocalDateTime date);

    List<Booking> findAllByItemIdInAndEndBookingAfterOrderByStartBookingDesc(List<Integer> lst, LocalDateTime date);

    List<Booking> findAllByItemIdInAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(List<Integer> lst,LocalDateTime dateStr, LocalDateTime dateEnd);

    List<Booking> findAllByItemIdAndStatusAndStartBookingBeforeOrderByStartBookingDesc(int itemId, BookingStatus status, LocalDateTime date);

    List<Booking> findAllByItemIdAndStatusAndStartBookingAfterOrderByStartBookingAsc(int itemId, BookingStatus status, LocalDateTime date);

    List<Booking> findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatus(int itemId, int userId, LocalDateTime now, BookingStatus approved);
}