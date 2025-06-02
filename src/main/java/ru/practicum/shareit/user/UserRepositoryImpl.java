package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    // хранилище пользователей
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return users.keySet().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public User findById(long id) {
        User user = users.get(id);
        if (Objects.isNull(user))
            throw new NotFoundException(String.format("Пользователь с ID %s не найден", id));
        return user;
    }

    @Override
    public User update(long id, User newUser) {
        User user = findById(id);
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        return user;
    }

    @Override
    public User delete(long id) {
        User user = findById(id);
        users.remove(id);
        return user;
    }

    @Override
    public Set<String> findAllEmail() {
        // создаю множество-хранилище уникальных почтовых адресов пользователей
        return users.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> findAllUsers() {
        return new HashSet<>(users.keySet());
    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
