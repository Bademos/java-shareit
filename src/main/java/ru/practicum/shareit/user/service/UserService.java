package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    public List<User> getAll();

    public void removeUser(int id);

    public User create(User user);

    public User update(User user);

    public User getById(int id);
}