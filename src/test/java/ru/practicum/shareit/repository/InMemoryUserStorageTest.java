package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;
import ru.practicum.shareit.utils.GeneratorId;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class InMemoryUserStorageTest {
    private UserRepositoryDb userStorage;
    private User correctUser;
    private User updateCorrectUser;
    private User checkCorrectUser;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserRepositoryDb() {
            @Override
            public Optional<User> findByEmail(String email) {
                return Optional.empty();
            }

            @Override
            public List<User> findAll() {
                return null;
            }

            @Override
            public List<User> findAll(Sort sort) {
                return null;
            }

            @Override
            public List<User> findAllById(Iterable<Integer> integers) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends User> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
                return null;
            }

            @Override
            public void deleteAllInBatch(Iterable<User> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Integer> integers) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public User getOne(Integer integer) {
                return null;
            }

            @Override
            public User getById(Integer integer) {
                return null;
            }

            @Override
            public User getReferenceById(Integer integer) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public Page<User> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> S save(S entity) {
                return null;
            }

            @Override
            public Optional<User> findById(Integer integer) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Integer integer) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Integer integer) {

            }

            @Override
            public void delete(User entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Integer> integers) {

            }

            @Override
            public void deleteAll(Iterable<? extends User> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends User> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends User> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }
        } ;
        correctUser = User.builder()
                .name("Test")
                .email("test@test.com")
                .build();
        checkCorrectUser = User.builder()
                .id(1)
                .name("Test")
                .email("test@test.com")
                .build();
        updateCorrectUser = User.builder()
                .id(1)
                .name("TestUpdate")
                .email("test@test.com")
                .build();
    }

    @Test
    void addCorrectUser() {
        userStorage.save(correctUser);
        assertEquals(userStorage.findById(checkCorrectUser.getId()).get().getId(),checkCorrectUser.getId());
        //assertTrue(userStorage.findById(checkCorrectUser.getId()).);
    }

    @Test
    void removeCorrectUser() {
        userStorage.save(correctUser);
        userStorage.remove(checkCorrectUser);
        assertFalse(userStorage.contains(checkCorrectUser.getId()).);
    }

    @Test
    void removeCorrectUserById() {
        userStorage.save(correctUser);
        userStorage.delete(checkCorrectUser);
        assertFalse(userStorage.findById(checkCorrectUser.getId()).isEmpty());
    }

    @Test
    void updateCorrectUser() {
        userStorage.save(correctUser);
        userStorage.save(updateCorrectUser);
        assertEquals(updateCorrectUser.getId(), userStorage.get(updateCorrectUser.getId()).getId());
        assertEquals(updateCorrectUser.getName(), userStorage.get(updateCorrectUser.getId()).getName());
        assertEquals(updateCorrectUser.getEmail(), userStorage.get(updateCorrectUser.getId()).getEmail());
    }

    @Test
    void getAll() {
        userStorage.save(correctUser);
        int sizeStorage = 1;
        assertEquals(sizeStorage, userStorage.findAll().size());
    }

    @Test
    void get() {
        userStorage.add(correctUser);
        assertEquals(checkCorrectUser.getEmail(), userStorage.f(checkCorrectUser.getId()).getEmail());
        assertEquals(checkCorrectUser.getName(), userStorage.get(checkCorrectUser.getId()).getName());
    }


    @Test
    void isContains() {
        userStorage.save(correctUser);
        assertTrue(userStorage.contains(checkCorrectUser.getEmail()));
    }

    @Test
    void isNotContains() {
        userStorage.save(correctUser);
        assertFalse(userStorage.contains("text@text.com"));
    }
}