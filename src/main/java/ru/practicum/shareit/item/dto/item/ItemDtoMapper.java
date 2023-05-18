package ru.practicum.shareit.item.dto.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDtoMapper {
    public static ItemDto makeItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item makeItemFromDto(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .itemRequest(null)
                .available(itemDto.getAvailable())
                .owner(user).build();
    }

    public static Item makeItemFromDto(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .itemRequest(itemRequest)
                .available(itemDto.getAvailable())
                .owner(user).build();
    }

    public static ItemDtoOut makeItemDtoOutFromItem(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}