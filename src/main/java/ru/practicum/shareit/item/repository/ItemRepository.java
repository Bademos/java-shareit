package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    public Item add(Item item);

    public Item update(Item item);

    public Item findById(int id);

    public void delete(int id);

    public List<Item> getAll();

    public List<Item> getAllByUser(int id);
}
