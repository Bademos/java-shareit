package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int id = 0;

    @Override
    public User add(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = findById(user.getId());
        emails.remove(oldUser.getEmail());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Invalid UserID");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void removeUser(int id) {
        User oldUser = findById(id);
        emails.remove(oldUser.getEmail());
        users.remove(id);
    }

    @Override
    public boolean containEmail(String ml) {
        return emails.contains(ml);
    }

    @Override
    public Set<String> getListOfEmail() {
        return emails;
    }

    private int generateId() {
        id += 1;
        return id;
    }
}