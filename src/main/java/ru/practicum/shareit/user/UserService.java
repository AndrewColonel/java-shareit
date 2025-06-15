package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(long id);

    UserDto updateUser(long id, UserPatchDto userDto);

    UserDto deleteUser(long id);
}
