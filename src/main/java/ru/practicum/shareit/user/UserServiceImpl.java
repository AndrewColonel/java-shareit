package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static ru.practicum.shareit.user.UserCheck.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        // UserDto - един для POST и PATCH, поэтому форматно-логический контроль
        // делаю в сервисе
        isUserDto(userDto);
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
        return UserMapper.toUserDto(userRepository.findById(id));
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        isGetNameEmail(userDto);
        User oldUser = userRepository.findById(id);
        return UserMapper.toUserDto(
                userRepository.update(id, oldUser,
                        UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto deleteUser(long id) {
        return UserMapper.toUserDto(userRepository.delete(id));
    }

}
