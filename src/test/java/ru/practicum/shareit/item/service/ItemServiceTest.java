package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private static User userA;

    private static User userB;

    private static Item itemA;

    private static Item itemB;

    private static Item updatedItem;


    @BeforeAll
    public static void beforeAll() {
        userA = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        userB = User.builder()
                .id(2)
                .name("user2")
                .email("user2@user.com")
                .build();

        itemA = Item.builder()
                .id(1)
                .name("test")
                .description("test test")
                .available(false)
                .owner(userA)
                .build();

        itemB = Item.builder()
                .id(2)
                .name("test")
                .description("test test")
                .available(false)
                .owner(userB)
                .build();

        updatedItem = Item.builder()
                .id(1)
                .name("update")
                .description("test test")
                .available(false)
                .owner(userA)
                .build();

    }

    @BeforeEach
    public void beforeEach() {
        userService.create(userA);
    }

    @Test
    void addItem() {
        var item = itemService.create(itemA);

        assertEquals(item.getId(), itemA.getId());
        assertEquals(item.getName(), itemA.getName());
        assertEquals(item.getDescription(), itemA.getDescription());

    }

    @Test
    void createItemTest() {
        Item item = itemService.create(itemA);

        assertEquals(item.getId(), itemA.getId());
        assertEquals(item.getName(), itemA.getName());
        assertEquals(item.getDescription(), itemA.getDescription());
    }



    @Test
    void updateTest() {
        Item item = itemService.create(itemA);
        Item updateItem = itemService.update(item.getId(), ItemServiceTest.updatedItem);

        assertEquals(updateItem.getId(), ItemServiceTest.updatedItem.getId());
        assertEquals(updateItem.getName(), ItemServiceTest.updatedItem.getName());
        assertEquals(updateItem.getDescription(), ItemServiceTest.updatedItem.getDescription());
    }

    @Test
    void getByIdTest() {
        itemService.create(itemA);
        ItemDto item = itemService.getById(itemA.getId(), itemA.getOwner().getId());

        assertEquals(item.getId(), itemA.getId());
        assertEquals(item.getName(), itemA.getName());
        assertEquals(item.getDescription(), itemA.getDescription());
    }

    @Test
    void getByIdFalseTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.getById(itemA.getId(), itemA.getOwner().getId()));
    }

    @Test
    void getAllByUserTest() {
        itemService.create(itemA);
        List<ItemDto> items = itemService.getAllByUser(itemA.getId());

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemA.getId());
        assertEquals(items.get(0).getName(), itemA.getName());
        assertEquals(items.get(0).getDescription(), itemA.getDescription());
    }

    @Test
    void deleteTest() {
        itemService.create(itemA);

        itemService.removeItem(itemA.getId());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemA.getId(), itemA.getOwner().getId()));

    }
}