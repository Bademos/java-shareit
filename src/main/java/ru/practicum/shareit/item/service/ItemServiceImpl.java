package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepositoryDb itemRepository;
    UserRepositoryDb userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;
    Locale russianLocal = new Locale("ru");

    @Autowired
    public ItemServiceImpl(ItemRepositoryDb itemRepository, UserRepositoryDb userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll().stream().map(ItemDtoMapper::makeItemDto).map(this::addComments).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByUser(int userId) {
        User owner = getUserByIdWithCheck(userId);
        return itemRepository.findAllByOwner(owner)
                .stream()
                .map(ItemDtoMapper::makeItemDto)
                .map(this::addComments)
                .map(this::getWithBooking)
                .collect(Collectors.toList());
    }

    @Override
    public void removeItem(int id) {
        itemRepository.delete(getItemByIdWithCheck(id));
    }

    @Override
    public Item create(Item item) {
        userRepository.findById(item.getOwner().getId());
        return itemRepository.save(item);
    }

    @Override
    public Item update(int id, Item item) {
        Item oldItem = itemRepository.findById(id).orElse(null);
        assert oldItem != null;
        checkUserOwner(oldItem.getOwner().getId(), item.getOwner().getId());
        oldItem = updateSrv(oldItem, item);
        itemRepository.save(oldItem);
        return oldItem;
    }

    @Override
    public List<Item> search(String text) {
        final var tempText = text.toLowerCase(russianLocal);
        return itemRepository.findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase(russianLocal).contains(tempText)
                        || item.getName().toLowerCase(russianLocal).contains(tempText))
                .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(CommentDto commentDto, int userId, int itemId) {
        User user = getUserByIdWithCheck(userId);
        Item item = getItemByIdWithCheck(itemId);
        checkUserAndItem(userId, itemId);
        Comment comment = CommentDtoMapper.makeComment(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }


    @Override
    public ItemDto getById(int id, int userId) {
        Item item = getItemByIdWithCheck(id);
        ItemDto itemDto = ItemDtoMapper.makeItemDto(item);
        if (item.getOwner().getId() == userId) {
            getWithBooking(itemDto);
        }
        addComments(itemDto);
        return itemDto;
    }

    private ItemDto getWithBooking(ItemDto item) {
        List<Booking> lastBookings = bookingRepository
                .findAllByItemIdAndStatusAndStartBookingBeforeOrderByStartBookingDesc(item.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());
        System.out.println(lastBookings);
        if (!lastBookings.isEmpty()) {
            BookingDtoForItem last = BookingDtoForItem.builder().id(lastBookings.get(0).getId())
                    .bookerId(lastBookings.get(0).getUser().getId()).build();
            item.setLastBooking(last);
            System.out.println(last);
        }
        List<Booking> nextBookings = bookingRepository
                .findAllByItemIdAndStatusAndStartBookingAfterOrderByStartBookingAsc(item.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());
        System.out.println(nextBookings);
        if (!nextBookings.isEmpty()) {
            BookingDtoForItem next = BookingDtoForItem.builder().id(nextBookings.get(0).getId())
                    .bookerId(nextBookings.get(0).getUser().getId()).build();
            item.setNextBooking(next);
            System.out.println(next);
        }
        return item;
    }

    private ItemDto addComments(ItemDto itemDto) {
        List<CommentDto> comments = commentRepository
                .findAllByItemIdOrderByIdAsc(itemDto.getId())
                .stream()
                .map(CommentDtoMapper::makeCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }

    private void checkUserOwner(int id, int userId) {
        if (id != userId) {
            throw new NotFoundException("The User has no the item");
        }
    }


    public Item updateSrv(Item oldItem, Item newItem) {
        Item itemUpd = Item.builder()
                .id(oldItem.getId())
                .name(oldItem.getName())
                .description(oldItem.getDescription())
                .owner(oldItem.getOwner())
                .available(oldItem.getAvailable())
                .build();
        var tempItem = oldItem.toBuilder();

        if (newItem.getName() != null) {
            itemUpd.setName(newItem.getName());
            tempItem.name(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            itemUpd.setDescription(newItem.getDescription());
            tempItem.description(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            itemUpd.setAvailable(newItem.getAvailable());
            tempItem.available(newItem.getAvailable());
        }
        return tempItem.build();
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

    private void checkUserAndItem(int userId, int itemId) {
        List<Booking> bookingsUserItems =
                bookingRepository.findBookingByItemIdAndUserIdAndEndBookingBeforeAndStatus(itemId, userId,
                        LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookingsUserItems.isEmpty()) {
            throw new ValidationException("The user has no bookings of the item");
        }
    }
}