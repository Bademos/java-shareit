package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, Map<Integer, Item>> itemsByUser = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(getId());
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

    private int getId() {
        int lastId = items.values()
                .stream()
                .mapToInt(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    private void containUserHandler(Item item) {
        if (!itemsByUser.containsKey(item.getOwnerId())) {
            itemsByUser.put(item.getOwnerId(), new HashMap<>());
        }
    }

    @Override
    public List<Item> search(String text) {
        final var russianLocal = new Locale("ru");
        final var tempText = text.toLowerCase(russianLocal);
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase(russianLocal).contains(tempText)
                        || item.getName().toLowerCase(russianLocal).contains(tempText))
                .collect(Collectors.toList());
    }
}