package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    List<User> findAll();

    User findById(long id);

    User update(long id, User oldUser, User newUser);

    User delete(long id);

   }
