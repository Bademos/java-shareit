package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, Map<Integer, Item>> itemsByUser = new HashMap<>();

    @Override
    public Item add(Item item) {
        items.put(item.getId(), item);
        containUserHandler(item);
        itemsByUser.get(item.getOwnerId()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        containUserHandler(item);
        itemsByUser.get(item.getOwnerId()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(int id) {
        return items.get(id);
    }

    @Override
    public void delete(int id) {
        if (items.containsKey(id)) {
            items.remove(id);
        } else {
            throw new ValidationException("Incorrect id");
        }
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByUser(int id) {
        return new ArrayList<>(itemsByUser.get(id).values());
    }

    private void containUserHandler(Item item) {
        if (!itemsByUser.containsKey(item.getOwnerId())) {
            itemsByUser.put(item.getOwnerId(), new HashMap<>());
        }
    }
}