package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    User save(User user);

    Collection<User> findAll();

    User findById(long id);

    User update(long id, User user);

    User delete(long id);

    Collection<String> findAllEmail();

    Collection<Long> findAllUsers();
}
