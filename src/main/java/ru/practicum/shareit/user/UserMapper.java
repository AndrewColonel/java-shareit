package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        // при создании пользователя валидация данных полей будет выполнена аннотациями
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static User toUser(User user, UserPatchDto userPatchDto) {
        // при обновлении пользователя валидация данных полей будет выполнена в серисе
        if (Objects.nonNull(userPatchDto.getName())) user.setName(userPatchDto.getName());
        if (Objects.nonNull(userPatchDto.getEmail())) user.setEmail(userPatchDto.getEmail());
        return user;
    }


}
