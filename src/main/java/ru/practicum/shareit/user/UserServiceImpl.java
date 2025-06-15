package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static ru.practicum.shareit.user.UserCheck.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id))));
    }

    @Override
    public UserDto updateUser(long id, UserPatchDto userPatchDto) {
        isUserPatchDto(userPatchDto);
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id)));
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(user, userPatchDto)));
    }

    @Override
    public UserDto deleteUser(long id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", id)));
        userRepository.delete(userToDelete);
        return UserMapper.toUserDto(userToDelete);
    }

}
