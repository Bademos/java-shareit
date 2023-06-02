package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ItemRepositoryTest {
    @Autowired
    ItemRepositoryDb itemRepository;

    Item itemA;
    Item itemB;

    @Test
    void findById() {
        User user = User.builder()
                .id(1)
                .name("Bademus")
                .email("cur@cur.com")
                .build();

        itemA = Item.builder()
                .id(1)
                .name("Das Ding")
                .description("boring")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(itemA);

        Item itemResponse = itemRepository.findById(itemA.getId()).orElseThrow();
        assertEquals(itemResponse.getId(), itemA.getId());
        assertEquals(itemResponse.getName(), itemA.getName());
        assertEquals(itemResponse.getAvailable(), itemA.getAvailable());
        assertEquals(itemResponse.getOwner(), user);
    }

    @Test
    void findAllByOwner() {
        User user = User.builder()
                .id(1)
                .name("Bademus")
                .email("cur@cur.com")
                .build();

        itemA = Item.builder()
                .id(1)
                .name("Das Ding")
                .description("boring")
                .available(true)
                .owner(user)
                .build();
        itemB = Item.builder()
                .id(2)
                .name("Das Ring")
                .description("firing")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(itemA);
        itemRepository.save(itemB);
        List<Item> res = itemRepository.findByOwnerOrderByIdAsc(user);
        assertEquals(2,res.size());
    }

    @Test
    void deleteTest() {
        User user = User.builder()
                .id(1)
                .name("Bademus")
                .email("cur@cur.com")
                .build();

        itemA = Item.builder()
                .id(1)
                .name("Das Ding")
                .description("boring")
                .available(true)
                .owner(user)
                .build();
        itemB = Item.builder()
                .id(2)
                .name("Das Ring")
                .description("firing")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(itemA);
        itemRepository.save(itemB);
        List<Item> res = itemRepository.findByOwnerOrderByIdAsc(user);
        assertEquals(2,res.size());

        itemRepository.delete(itemB);

        res = itemRepository.findByOwnerOrderByIdAsc(user);
        assertEquals(1,res.size());
    }
}