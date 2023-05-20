package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ItemRepositoryTest {
    @Autowired
    ItemRepositoryDb itemRepository;

    Item item;

    @Test
    void findById() {
        User user = User.builder()
                .id(1)
                .name("Bademus")
                .email("cur@cur.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Das Ding")
                .description("boring")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item);

        Item itemResponse = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(itemResponse.getId(), item.getId());
        assertEquals(itemResponse.getName(), item.getName());
        assertEquals(itemResponse.getAvailable(), item.getAvailable());
        assertEquals(itemResponse.getOwner(), user);
    }
}
