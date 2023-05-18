
package ru.practicum.shareit.request.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional

public class ItemRequestRepositoryTest {
    @Autowired
    UserRepositoryDb userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    ItemRepositoryDb itemRepository;

    User userA;
    User userB;
    Item item;
    Item item2;
    ItemRequest itemRequest;


    @BeforeEach
    void beforeEach() {
        userA = User.builder().id(1)
                .email("a@caca.com")
                .name("A")
                .build();
        userB = User.builder().id(2)
                .email("b@caca.com")
                .name("B")
                .build();

        item = Item.builder()
                .id(1)
                .name("Вешь")
                .description("Вещь в себе")
                .available(true)
                .owner(userA)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("boring")
                .requestor(userA)
                .created(LocalDateTime.of(2022, 2, 24, 5, 0, 1))
                .build();

        itemRepository.save(item);
        userRepository.save(userA);
        userRepository.save(userB);
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void getAllTest() {

        assertEquals(itemRequestRepository.findAll().get(0).getDescription(), "boring");
    }

    @Test
    void findItemRequestByRequestorIdNot() {
        var itemRequests = itemRequestRepository.findAllByRequestorNot(userB,
                PageRequest.of(0,1, Sort.by(Sort.Direction.ASC, "created")));

        assertEquals(itemRequests.getContent().size(), 1);
    }

}


