package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DoubleEntityException;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private static int id = 0;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();
    }

    @Override
    public void removeUser(int id) {
        repository.removeUser(id);
    }

    @Override
    public User create(User user) {
        emailCheck(user.getEmail());
        user.setId(generateId());
        return repository.add(user);
    }

    @Override
    public User update(User user) {
        System.out.println(user);
        if (user.getEmail() != null
                && !user.getEmail().isEmpty() && !user.getEmail().equals(repository.findById(user.getId()).getEmail())) {
            emailCheck(user.getEmail());
        }
        User oldUser = repository.findById(user.getId());
        System.out.println(oldUser);
        User oldUpd = UserDtoMapper.update(oldUser, user);
        System.out.println(oldUpd);
        return repository.update(oldUpd);
    }

    @Override
    public User getById(int id) {
        return repository.findById(id);
    }

    private void emailCheck(String email) {
        boolean isCheck = repository.getAll().stream().anyMatch(user -> user.getEmail().equals(email));
        if (isCheck) {
            throw new DoubleEntityException("Incorrect email address");
        }
    }

    private int generateId() {
        id += 1;
        return id;
    }
}