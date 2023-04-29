package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DoubleEntityException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

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
        return repository.add(user);
    }

    @Override
    public User update(User user) {
        if (user.getEmail() != null
                && !user.getEmail().isEmpty() && !user.getEmail().equals(repository.findById(user.getId()).getEmail())) {
            emailCheck(user.getEmail());
        }
        User oldUser = repository.findById(user.getId());
        User oldUpd = updateSrv(oldUser, user);
        return repository.update(oldUpd);
    }

    @Override
    public User getById(int id) {
        return repository.findById(id);
    }

    private void emailCheck(String email) {
        if (repository.containEmail(email)) {
            throw new DoubleEntityException("Incorrect email address");
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