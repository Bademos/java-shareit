package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
     ItemRepository itemRepository;
     UserRepository userRepository;
     Locale RUSSIAN_LOCAL = new Locale("ru");

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
        userRepository.findById(item.getOwner().getId());
        return itemRepository.add(item);
    }

    @Override
    public Item update(int id, Item item) {
        var oldItem = itemRepository.findById(id);
        checkUserOwner(oldItem.getOwner().getId(), item.getOwner().getId());
        oldItem = updateSrv(oldItem, item);
        itemRepository.update(oldItem);
        return oldItem;
    }

    @Override
    public List<Item> search(String text) {
        final var tempText = text.toLowerCase(RUSSIAN_LOCAL);
        return itemRepository.getAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase(RUSSIAN_LOCAL).contains(tempText)
                        || item.getName().toLowerCase(RUSSIAN_LOCAL).contains(tempText))
                .collect(Collectors.toList());
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


    public Item updateSrv(Item oldItem, Item newItem) {
        Item itemUpd = Item.builder()
                .id(oldItem.getId())
                .name(oldItem.getName())
                .description(oldItem.getDescription())
                .owner(oldItem.getOwner())
                .available(oldItem.getAvailable())
                .build();
        var tempItem = oldItem.toBuilder();

        if (newItem.getName() != null) {
            itemUpd.setName(newItem.getName());
            tempItem.name(newItem.getName());
        }
        if (newItem.getDescription() != null) {
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