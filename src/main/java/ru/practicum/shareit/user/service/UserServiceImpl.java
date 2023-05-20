package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryDb repository;

    @Autowired
    public UserServiceImpl(UserRepositoryDb repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void removeUser(int id) {
        repository.delete(getById(id));
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        User oldUser = repository.findById(user.getId()).orElseThrow();
        User oldUpd = updateSrv(oldUser, user);
        return repository.save(oldUpd);
    }

    @Override
    public User getById(int id) {
        if (repository.findById(id).isPresent()) {
            return repository.findById(id).orElseThrow();
        } else {
            throw new NotFoundException("User with id" + id + "is not found");
        }
    }

    public User updateSrv(User oldUser, User newUser) {
        var tempUser = oldUser.toBuilder();
        if (newUser.getName() != null && !newUser.getName().isEmpty()) {
            tempUser.name(newUser.getName());
        }
        if (newUser.getEmail() != null
                && !newUser.getEmail().isEmpty()) {
            tempUser.email(newUser.getEmail());
        }
        return tempUser.build();
    }
}