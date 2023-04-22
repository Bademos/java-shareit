package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Item>> itemsByUserInList = new HashMap<>();
    private int id = 0;

    @Override
    public Item add(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        containUserHandler(item);
        itemsByUserInList.get(item.getOwner().getId()).add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        containUserHandler(item);
        List<Item> items =itemsByUserInList.get(item.getOwner().getId());
        Item oldItem = items
                .stream()
                .filter(it -> it.getId() == item.getId())
                .collect(Collectors.toList())
                .get(0);
        items.remove(oldItem);
        items.add(item);
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
        return itemsByUserInList.get(id);
    }

    private void containUserHandler(Item item) {
        if (!itemsByUserInList.containsKey(item.getOwner().getId())) {
            itemsByUserInList.put(item.getOwner().getId(), new ArrayList<>());
        }
    }

    private int generateId() {
        id += 1;
        return id;
    }

}