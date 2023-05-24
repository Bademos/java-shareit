package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ItemServiceMockTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepositoryDb userRepository;

    @Mock
    private ItemRepositoryDb itemRepository;

    @Mock
    private CommentRepository commentRepository;


    private User userA;

    private User userB;

    private Item itemA;

    private Item itemB;

    private Item updatedItem;

    private Comment comment;

    private CommentDto commmentDto;


    @BeforeEach
    public void beforeAll() {
        userA = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        userB = User.builder()
                .id(2)
                .name("user2")
                .email("user2@user.com")
                .build();

        itemA = Item.builder()
                .id(1)
                .name("test")
                .description("test test")
                .available(false)
                .owner(userA)
                .build();

        itemB = Item.builder()
                .id(2)
                .name("test")
                .description("test test")
                .available(false)
                .owner(userB)
                .build();

        updatedItem = Item.builder()
                .id(1)
                .name("update")
                .description("test test")
                .available(false)
                .owner(userA)
                .build();

        comment = Comment.builder()
                .text("test")
                .item(itemA)
                .user(userA)
                .build();
        commmentDto = CommentDtoMapper.makeCommentDto(comment);

    }


    @Test
    void createItemMockTest() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userA));
        when(itemRepository.save(any())).thenReturn(itemA);
        var item = itemService.create(itemA);

        assertEquals(item.getId(), itemA.getId());
        assertEquals(item.getName(), itemA.getName());
        assertEquals(item.getDescription(), itemA.getDescription());
    }


    @Test
    void updateItemMockTest() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userA));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(updatedItem));
        when(itemRepository.save(any())).thenReturn(updatedItem);
        var item = itemService.create(updatedItem);
        Item updateItem = itemService.update(updatedItem.getId(), updatedItem);

        assertEquals(updateItem.getId(), updatedItem.getId());
        assertEquals(updateItem.getName(), updatedItem.getName());
        assertEquals(updateItem.getDescription(), updatedItem.getDescription());
    }


    @Test
    void getByIdTest() {
        when(bookingRepository.findTopByItemIdAndStatusAndStartBookingBefore(anyInt(), any(), any(), any())).thenReturn(Optional.ofNullable(Booking
                .builder()
                .id(1)
                .startBooking(LocalDateTime.now().minusDays(28))
                .endBooking(LocalDateTime.now().minusDays(14))
                .item(itemA)
                .user(userB)
                .build()));
        when(bookingRepository.findTopByItemIdAndStatusAndStartBookingAfter(anyInt(), any(), any(), any())).thenReturn(Optional.ofNullable(Booking
                .builder()
                .id(1)
                .startBooking(LocalDateTime.now().plusDays(14))
                .endBooking(LocalDateTime.now().plusDays(28))
                .item(itemA)
                .user(userB)
                .build()));

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userB));
        itemService.create(itemA);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(itemA));
        ItemDto item = itemService.getById(itemA.getId(), itemA.getOwner().getId());
        assertEquals(item.getId(), itemA.getId());
        assertEquals(item.getName(), itemA.getName());
        assertEquals(item.getDescription(), itemA.getDescription());
    }

    @Test
    void getByIdFalseTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.getById(itemA.getId(), itemA.getOwner().getId()));
    }


    @Test
    void getAllByUserTest() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userA));
        when(itemRepository.findAllByOwner(any())).thenReturn(Collections.singletonList(itemA));
        List<ItemDto> items = itemService.getAllByUser(itemA.getId());
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemA.getId());
        assertEquals(items.get(0).getName(), itemA.getName());
        assertEquals(items.get(0).getDescription(), itemA.getDescription());
    }

    @Test
    void getAllTest() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemA));
        itemService.create(itemA);
        List<ItemDto> items = itemService.getAll();
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemA.getId());
        assertEquals(items.get(0).getName(), itemA.getName());
        assertEquals(items.get(0).getDescription(), itemA.getDescription());
    }

    @Test
    void searchTest() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemA));
        assertEquals(itemService.search("tst").size(), 0);
    }

    @Test
    void createCommentWithOutBookingTest() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userA));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(itemA));
        assertThrows(ValidationException.class, () -> itemService.createComment(commmentDto, userA.getId(), itemA.getId()));
    }
}