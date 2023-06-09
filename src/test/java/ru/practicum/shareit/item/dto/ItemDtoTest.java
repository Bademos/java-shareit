package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDtoTest {
        static User userA;
        static User userB;
        static Item item;
        static ItemDto itemDto;
        final LocalDateTime startDate =  LocalDateTime.of(2023,1,1,11,11,11);
        final LocalDateTime endDate =  LocalDateTime.of(2023,2,2,2,22,22);

        @BeforeAll
        static void setUp() {
            userA = User.builder()
                    .id(1)
                    .name("userA")
                    .email("cu@cu.com")
                    .build();
            userB = User.builder()
                    .id(1)
                    .name("userB")
                    .email("ca@ca.com")
                    .build();
            item = Item.builder()
                    .id(1)
                    .name("item")
                    .available(Boolean.TRUE)
                    .owner(userB)
                    .description("booring")
                    .build();
        }

        @Test
        void itemFromItemDtoTest() {

            ItemDto itemDto = ItemDto.builder()
                    .id(1)
                    .name("item")
                    .description("strange")
                    .available(true)
                    .build();

            Item itemResponse = ItemDtoMapper.makeItemFromDto(itemDto, userA);
            assertEquals(itemResponse.getName(), itemDto.getName());
            assertEquals(itemResponse.getDescription(), itemDto.getDescription());
            assertEquals(itemResponse.getOwner(),userA);
        }

    @Test
    void itemDtoFromItemTest() {
        ItemDto itemDtoResponse = ItemDtoMapper.makeItemDto(item);
        assertEquals(item.getName(), itemDtoResponse.getName());
        assertEquals(item.getDescription(), itemDtoResponse.getDescription());
        assertEquals(itemDtoResponse.getId(),item.getId());
    }

    @Test
    void commentDtoMakeTest() {
        Comment comment = Comment.builder()
                .id(1)
                .item(Item.builder().id(1).build())
                .user(User.builder().id(1).build())
                .created(LocalDateTime.now())
                .build();
        CommentDto commentDto = CommentDtoMapper.makeCommentDto(comment);
        assertEquals(commentDto.getItemId(), comment.getItem().getId());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

    @Test
    void commentMakeFromDtoTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .itemId(1)
                .authorId(1)
                .text("boring")
                .build();
        Comment comment = CommentDtoMapper.makeComment(commentDto,
                User.builder().id(1).build(),
                Item.builder().id(1).build());
        assertEquals(commentDto.getItemId(), comment.getItem().getId());
        assertEquals(commentDto.getAuthorId(), comment.getUser().getId());
    }
}
