package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
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

    Page<Booking> findAllByUserId(int userId, Pageable pager);

    Page<Booking> findAllByUserIdAndStatus(int userId, BookingStatus status, Pageable pager);

    Page<Booking> findAllByUserIdAndEndBookingBefore(int userId, LocalDateTime date, Pageable pager);

    Page<Booking> findAllByUserIdAndEndBookingAfter(int userId, LocalDateTime date, Pageable pager);

    Page<Booking> findAllByUserIdAndStartBookingBeforeAndEndBookingAfter(int userId, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pager);

    Page<Booking> findAllByItemIdIn(List<Integer> lst, Pageable pager);

    Page<Booking> findAllByItemIdInAndStatus(List<Integer> lst, BookingStatus status, Pageable pager);

    Page<Booking> findAllByItemIdInAndEndBookingBefore(List<Integer> lst, LocalDateTime date, Pageable pager);

    Page<Booking> findAllByItemIdInAndEndBookingAfter(List<Integer> lst, LocalDateTime date, Pageable pager);

    Page<Booking> findAllByItemIdInAndStartBookingBeforeAndEndBookingAfter(List<Integer> lst, LocalDateTime dateStr, LocalDateTime dateEnd, Pageable pager);

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