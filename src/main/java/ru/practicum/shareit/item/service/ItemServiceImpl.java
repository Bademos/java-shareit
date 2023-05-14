package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.util.ConstantsShare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepositoryDb itemRepository;
    UserRepositoryDb userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryDb itemRepository, UserRepositoryDb userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(ItemDtoMapper::makeItemDto).collect(Collectors.toList());
        addCommentsToList(items);
        return items;
    }

    @Override
    public List<ItemDto> getAllByUser(int userId) {
        User owner = getUserByIdWithCheck(userId);
        List<ItemDto> items = itemRepository.findAllByOwner(owner).stream()
                .map(ItemDtoMapper::makeItemDto).collect(Collectors.toList());

        List<Integer> itemIds = items.stream().map(ItemDto::getId).collect(Collectors.toList());
        Map<Integer, ItemDto> itemsMap = items.stream().collect(Collectors.toMap(ItemDto::getId, item -> item));
        List<CommentDto> comments = commentRepository.findAllComments(itemIds).stream()
                .map(CommentDtoMapper::makeCommentDto).collect(Collectors.toList());
        comments.forEach(comment -> itemsMap.get(comment.getItemId()).getComments().add(comment));

        List<Booking> lastBookings = bookingRepository.findBookingsLast(itemIds, LocalDateTime.now(), userId, PageRequest.of(0, 1));
        lastBookings.forEach(booking -> itemsMap.get(booking.getItem().getId()).setLastBooking(BookingDtoMapper.makeBookingDtoForItem(booking)));

        List<Booking> nextBookings = bookingRepository.findBookingsNext(itemIds, LocalDateTime.now(), userId, PageRequest.of(0, 1));
        nextBookings.forEach(booking -> itemsMap.get(booking.getItem().getId()).setNextBooking(BookingDtoMapper.makeBookingDtoForItem(booking)));
        return items;
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
        final var tempText = text.toLowerCase(ConstantsShare.russianLocal);
        return itemRepository.findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase(ConstantsShare.russianLocal).contains(tempText)
                        || item.getName().toLowerCase(ConstantsShare.russianLocal).contains(tempText))
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
        Booking lst = bookingRepository.findTopByItemIdAndStatusAndStartBookingBefore(item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now(),
                ConstantsShare.sortDesc).orElse(null);
        if (lst != null) {
            BookingDtoForItem last = BookingDtoMapper.makeBookingDtoForItem(lst);
            item.setLastBooking(last);
        }
        Booking nxt = bookingRepository.findTopByItemIdAndStatusAndStartBookingAfter(item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now(),
                ConstantsShare.sortAsc).orElse(null);
        if (nxt != null) {
            BookingDtoForItem next = BookingDtoMapper.makeBookingDtoForItem(nxt);
            item.setNextBooking(next);
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

    private void addCommentsToList(List<ItemDto> items) {
        List<Integer> itemIds = items.stream().map(ItemDto::getId).collect(Collectors.toList());
        Map<Integer, ItemDto> itemsMap = items.stream().collect(Collectors.toMap(ItemDto::getId, item -> item));
        List<CommentDto> comments = commentRepository.findAllComments(itemIds).stream()
                .map(CommentDtoMapper::makeCommentDto).collect(Collectors.toList());
        comments.forEach(comment -> itemsMap.get(comment.getItemId()).getComments().add(comment));
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