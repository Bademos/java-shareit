package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepositoryDb userRepository;

    private  User userA;
    private  User userB;


    @BeforeEach
    public void setUp() {
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
        when(userRepository.save(any())).thenReturn(userA);

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

        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException(" email error"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.create(userWithoutEmail));
    }


    @Test
    void updateWithOutUserTest() {
        assertThrows(
                NoSuchElementException.class,
                () -> userService.update(userA));
    }

    @Test
    void updateTest() {
        User updateCorrectUser = User.builder()
                .id(1)
                .name("updateuser")
                .email("user@user.com")
                .build();
        when(userRepository.save(any())).thenReturn(updateCorrectUser);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(userA));

        User updateUser = userService.update(updateCorrectUser);

        assertEquals(updateUser.getId(), updateCorrectUser.getId());
        assertEquals(updateUser.getName(), updateCorrectUser.getName());
        assertEquals(updateUser.getEmail(), updateCorrectUser.getEmail());
    }


    @Test
    void getByIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(userA));

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
        when(userRepository.findAll()).thenReturn(Collections.singletonList(userA));
        List<User> users = userService.getAll();

        assertEquals(users.size(), 1);
    }

}
