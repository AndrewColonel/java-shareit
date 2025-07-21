package ru.practicum.shareit.server.user;

import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserPatchDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(NewUserDto newUserDto);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(long id);

    UserDto updateUser(long id, UserPatchDto userDto);

    UserDto deleteUser(long id);
}
