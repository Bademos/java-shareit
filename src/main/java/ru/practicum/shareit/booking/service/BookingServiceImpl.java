package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepositoryDb userRepository;
    ItemRepositoryDb itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, UserRepositoryDb userRepository, ItemRepositoryDb itemRepository) {
        this.bookingRepository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDtoOut updateBooking(int id, boolean isApprove, int userId) {
        BookingDtoOut bookingDtoOut = findBookingById(id);
        User user = getUserByIdWithCheck(bookingDtoOut.getBooker().getId());
        Item item = getItemByIdWithCheck(bookingDtoOut.getItem().getId());
        Booking booking = BookingDtoMapper.makeBookingFromBookingDtoOut(bookingDtoOut, user, item);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User with id:" + userId + " is not owner booking:" + booking);
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new NotAvailableException("The booking status cannot  be changed");
        }

        if (isApprove) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingDtoMapper.makeBookingDtoOutFromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut createBooking(BookingDto bookingDto, int userId) {
        Item item = getItemByIdWithCheck(bookingDto.getItemId());
        User user = getUserByIdWithCheck(userId);
        checkTimeInterval(bookingDto);

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Item could not be booked by owner");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("The item with id " + item.getId() + "is not available");
        }
        Booking booking = BookingDtoMapper.makeBookingFromDto(bookingDto, user, item);
        booking = bookingRepository.save(booking);
        log.debug("Creating new booking " + booking);
        return BookingDtoMapper.makeBookingDtoOutFromBooking(booking);
    }

    @Override
    public BookingDtoOut getBookingById(int bookingId, int userId) {
        getUserByIdWithCheck(userId);

        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
            checkBookingByUser(booking, userId);
            return BookingDtoMapper.makeBookingDtoOutFromBooking(booking);
        } else {
            throw new NotFoundException("There is no booking with id" + bookingId);
        }
    }

    @Override
    public List<BookingDtoOut> getBookingByUser(int userId, State state) {
        getUserByIdWithCheck(userId);
        List<Booking> res;
        switch (state) {
            case ALL:
                res = bookingRepository.findAllByUserIdOrderByStartBookingDesc(userId);
                break;
            case WAITING:
                res = bookingRepository.findAllByUserIdAndStatusOrderByStartBookingDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                res = bookingRepository.findAllByUserIdAndStatusOrderByStartBookingDesc(userId, BookingStatus.REJECTED);
                break;
            case PAST:
                res = bookingRepository.findAllByUserIdAndEndBookingBeforeOrderByStartBookingDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                res = bookingRepository.findAllByUserIdAndEndBookingAfterOrderByStartBookingDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                res = bookingRepository.findAllByUserIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
        return res.stream().map(BookingDtoMapper::makeBookingDtoOutFromBooking).collect(Collectors.toList());
    }


    @Override
    public List<BookingDtoOut> getBookingForAllItemsByUser(int userId, State state) {
        getUserByIdWithCheck(userId);
        List<Booking> res;
        if (userRepository.findById(userId).isPresent()) {
            List<Integer> itemsId = itemRepository.findAllByOwner(userRepository.findById(userId).get())
                    .stream().map(Item::getId).collect(Collectors.toList());
            switch (state) {
                case ALL:
                    res = bookingRepository.findAllByItemIdInOrderByStartBookingDesc(itemsId);
                    break;
                case WAITING:
                    res = bookingRepository.findAllByItemIdInAndStatusOrderByStartBookingDesc(itemsId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    res = bookingRepository.findAllByItemIdInAndStatusOrderByStartBookingDesc(itemsId, BookingStatus.REJECTED);
                    break;
                case PAST:
                    res = bookingRepository.findAllByItemIdInAndEndBookingBeforeOrderByStartBookingDesc(itemsId, LocalDateTime.now());
                    break;
                case FUTURE:
                    res = bookingRepository.findAllByItemIdInAndEndBookingAfterOrderByStartBookingDesc(itemsId, LocalDateTime.now());
                    break;
                case CURRENT:
                    res = bookingRepository.findAllByItemIdInAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(itemsId, LocalDateTime.now(), LocalDateTime.now());
                    break;
                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        } else {
            throw new NotFoundException("User is absent");
        }
        return res.stream().map(BookingDtoMapper::makeBookingDtoOutFromBooking).collect(Collectors.toList());
    }

    public BookingDtoOut findBookingById(int id) {
        if (bookingRepository.findById(id).isPresent()) {
            return BookingDtoMapper.makeBookingDtoOutFromBooking(bookingRepository.findById(id).get());
        } else {
            throw new NotFoundException("There is no booking with id:" + id);
        }
    }

    public Item getItemByIdWithCheck(int itemId) {
        if (itemRepository.findById(itemId).isPresent()) {
            return itemRepository.findById(itemId).get();
        } else {
            throw new NotFoundException("There is no item with id: " + itemId);
        }
    }

    public User getUserByIdWithCheck(int userId) {
        if (userRepository.findById(userId).isPresent()) {
            return userRepository.findById(userId).get();
        } else {
            throw new NotFoundException("There is no user with id: " + userId);
        }
    }

    public void checkTimeInterval(BookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new TimeIntervalException("Incorrect time limits");
        }
    }

    public void checkBookingByUser(Booking booking, int userId) {
        if (booking.getUser().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("The booking not linked with user " + userId);
        }
    }
}