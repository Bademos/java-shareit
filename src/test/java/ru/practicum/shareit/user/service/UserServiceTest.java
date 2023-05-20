package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UserServiceTest {
    @Autowired
    private UserServiceImpl userService;

    private static User userA;
    private static User userB;


    @BeforeAll
    public static void beforeAll() {
        userA = User.builder()
                .id(1)
                .name("userA")
                .email("userA@user.com")
                .build();
        userB = User.builder()
                .id(2)
                .name("userB")
                .email("userB@user.com")
                .build();
    }

    @Test
    void createUserTest() {
        User user = userService.create(userA);

        assertEquals(user.getId(), userA.getId());
        assertEquals(user.getName(), userA.getName());
        assertEquals(user.getEmail(), userA.getEmail());
    }

    @Test
    void createUserWithoutEmailTest() {
        User userWithoutEmail = User.builder()
                .id(1)
                .name("user")
                .build();
        assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.create(userWithoutEmail));
    }


    @Test
    void updateWithOutUserTest() {
        assertThrows(
                NoSuchElementException.class,
                () -> userService.update( userA));
    }

    @Test
    void updateTest() {
        userService.create(userA);
        User updateCorrectUser = User.builder()
                .id(1)
                .name("updateuser")
                .email("user@user.com")
                .build();
        User updateUser = userService.update(updateCorrectUser);

        assertEquals(updateUser.getId(), updateCorrectUser.getId());
        assertEquals(updateUser.getName(), updateCorrectUser.getName());
        assertEquals(updateUser.getEmail(), updateCorrectUser.getEmail());
    }


    @Test
    void getByIdTest() {
        userService.create(userA);
        User user = userService.getById(userA.getId());

        assertEquals(user.getId(), userA.getId());
        assertEquals(user.getName(), userA.getName());
        assertEquals(user.getEmail(), userA.getEmail());
    }

    @Test
    void getByIdWithOutUserTest() {
        assertThrows(
                NotFoundException.class,
                () -> userService.getById(userA.getId()));
    }

    @Test
    void getAllTest() {
        userService.create(userA);
        List<User> users = userService.getAll();

        assertEquals(users.size(), 1);
    }

    @Test
    void removeUser() {
        userService.create(userA);
        User user = userService.getById(userA.getId());


        assertEquals(user.getId(), userA.getId());
        assertEquals(user.getName(), userA.getName());
        assertEquals(user.getEmail(), userA.getEmail());

        userService.removeUser(userA.getId());
        assertThrows(NotFoundException.class,() ->userService.getById(userA.getId()));

    }

}
