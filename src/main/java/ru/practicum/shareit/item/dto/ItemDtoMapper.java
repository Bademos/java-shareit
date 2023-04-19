package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemDtoMapper {
    public static final Map<Integer, ItemDto> itemDtoStorage = new HashMap();

    public static ItemDto makeItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder().
                id(item.getId()).
                name(item.getName()).
                description(item.getDescription()).available(item.getAvailable()).
                build();
        itemDtoStorage.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    public static Item makeItemFromDto(ItemDto itemDto, int userId) {
        return Item.builder().
                id(itemDto.getId()).
                name(itemDto.getName()).
                description(itemDto.getDescription()).available(itemDto.getAvailable()).
                ownerId(userId).build();
    }

    public static Item update(Item oldItem, Item newItem) {
        Item itemUpd = Item.builder().id(oldItem.getId()).
                name(oldItem.getName()).description(oldItem.getDescription()).
                ownerId(oldItem.getOwnerId()).available(oldItem.getAvailable()).build();
        var tempItem = oldItem.toBuilder();

        if (newItem.getName() != null && !newItem.getName().isEmpty()) {
            itemUpd.setName(newItem.getName());
            tempItem.name(newItem.getName());
        }
        if (newItem.getDescription() != null
                && !newItem.getDescription().isEmpty()) {
            itemUpd.setDescription(newItem.getDescription());
            tempItem.description(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            itemUpd.setAvailable(newItem.getAvailable());
            tempItem.available(newItem.getAvailable());
        }
        return tempItem.build();
    }
}