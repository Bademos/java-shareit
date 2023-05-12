package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    List<Booking> findAllByItemIdInAndStartBookingBeforeAndEndBookingAfter(List<Integer> lst, LocalDateTime dateStr, LocalDateTime dateEnd, Sort sort);

    List<Booking> findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatus(int itemId, int userId, LocalDateTime now, BookingStatus approved);

    Optional<Booking> findTopByItemIdAndStatusAndStartBookingBefore(int itemId, BookingStatus status, LocalDateTime date, Sort sort);

    Optional<Booking> findTopByItemIdAndStatusAndStartBookingAfter(int itemId, BookingStatus status, LocalDateTime date, Sort sort);

    @Query("SELECT booking "
            + " FROM Booking booking "
            + " where booking.startBooking < :now "
            + " AND booking.item.id IN :ids "
            + " AND booking.item.owner.id = :userId "
            + " AND booking.status = 'APPROVED' "
            + " ORDER BY booking.startBooking DESC ")
    List<Booking> findBookingsLast(@Param("ids") List<Integer> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") Integer userId,
                                   Pageable pageable);

    @Query("SELECT booking "
            + " FROM Booking booking "
            + " WHERE booking.item.id IN ?1 "
            + " AND booking.startBooking > ?2 "
            + " AND booking.item.owner.id = ?3 "
            + " AND booking.status = 'APPROVED' "
            + " ORDER BY booking.startBooking ASC ")
    List<Booking> findBookingsNext(List<Integer> ids,
                                   LocalDateTime now,
                                   Integer userId,
                                   Pageable pageable);

}