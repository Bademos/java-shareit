package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl itemRepository, UserRepositoryImpl userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.getAll();
    }

    @Override
    public List<Item> getAllByUser(int id) {
        return itemRepository.getAllByUser(id);
    }

    @Override
    public void removeItem(int id) {
        itemRepository.delete(id);
    }

    @Override
    public Item create(Item item) {
        userRepository.findById(item.getOwnerId());
        return itemRepository.add(item);
    }

    @Override
    public Item update(int id, Item item) {
        var oldItem = itemRepository.findById(id);
        checkUserOwner(oldItem.getOwnerId(), item.getOwnerId());
        oldItem = ItemDtoMapper.update(oldItem, item);
        itemRepository.update(oldItem);
        return oldItem;
    }

    public Item updateItem(int id, Item item) {
        var oldItem = itemRepository.findById(id);
        checkUserOwner(oldItem.getOwnerId(), item.getOwnerId());

        oldItem = ItemDtoMapper.update(oldItem, item);
        itemRepository.update(oldItem);
        return oldItem;
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

    @Override
    public Item getById(int id) {
        return itemRepository.findById(id);
    }

    private void checkUserExist(int userId) {
        userRepository.findById(userId);
    }

    private void checkUserOwner(int id, int userId) {
        if (id != userId) {
            throw new NotFoundException("The User has no the item");
        }
    }
}