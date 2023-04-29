package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    public List<Item> getAll();

    public List<Item> getAllByUser(int id);

    public void removeItem(int id);

    public Item create(Item item);

    public Item update(int id, Item item);

    public Item getById(int id);

    public List<Item> search(String text);
}