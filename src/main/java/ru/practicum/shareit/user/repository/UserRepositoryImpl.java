package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> email = new HashSet<>();

    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        email.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        email.add(user.getEmail());
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
        users.remove(id);
    }

    @Override
    public boolean containEmail(String ml) {
        return email.contains(ml);
    }


}