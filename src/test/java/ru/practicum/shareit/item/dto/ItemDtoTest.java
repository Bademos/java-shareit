package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDtoTest {
        static User userA;
        static User userB;
        static Item item;
        static ItemDto itemDto;
        final static LocalDateTime startDate =  LocalDateTime.of(2023,1,1,11,11,11);
        final static LocalDateTime endDate =  LocalDateTime.of(2023,2,2,2,22,22);
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
        void ItemFromItemDtoTest() {

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
    void ItemDtoFromItemTest() {
        ItemDto itemDtoResponse = ItemDtoMapper.makeItemDto(item);
        assertEquals(item.getName(), itemDtoResponse.getName());
        assertEquals(item.getDescription(), itemDtoResponse.getDescription());
        assertEquals(itemDtoResponse.getId(),item.getId());
    }
}
