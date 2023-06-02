package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepositoryDb extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerOrderByIdAsc(User owner);

    List<Item> findByOwner(User owner);
}
