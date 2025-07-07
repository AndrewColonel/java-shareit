package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl {
    // implements UserRepository
    // хранилище пользователей
    private final Map<Long, User> users = new HashMap<>();

    //    @Override
    public User save(User user) {
        // проверка на совпадение почты, далее буждет выполняться ср6едсвами БД
        isEqualEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    //    @Override
    public List<User> findAll() {
        return users.keySet().stream()
                .map(users::get)
                .toList();
    }

    //    @Override
    public User findById(long id) {
        User user = users.get(id);
        if (Objects.isNull(user))
            throw new NotFoundException(String.format("Пользователь с ID %s не найден", id));
        return user;
    }

    //    @Override
    public User update(long id, User oldUser, User newUser) {
        // проверка на совпадение почты, далее буждет выполняться ср6едсвами БД
        if (Objects.nonNull(newUser.getEmail())) isEqualEmail(newUser.getEmail());
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        return oldUser;
    }

    //    @Override
    public User delete(long id) {
        User user = findById(id);
        users.remove(id);
        return user;
    }

    // вспомогательный метод, создает множество-хранилище уникальных почтовых адресов пользователей
    public void isEqualEmail(String userEmail) {
        if (users.values().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(email -> email.equals(userEmail)))
            throw new DuplicatedDataException(String.format("Пользователь с такой почтой %s уже есть",
                    userEmail));
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
