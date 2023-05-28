package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    public List<ItemDto> getAll();

    public List<ItemDto> getAllByUser(int id);

    public void removeItem(int id);

    public Item create(Item item);

    public Item update(int id, Item item);

    public ItemDto getById(int id, int userId);

    public List<Item> search(String text);

    public Comment createComment(CommentDto commentDto, int userId, int itemId);
}