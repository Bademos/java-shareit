package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface UserRepository {
    public User add(User user);

    public User update(User user);

    public User findById(int id);

    public List<User> getAll();

    public void removeUser(int id);

    public boolean containEmail(String email);
}