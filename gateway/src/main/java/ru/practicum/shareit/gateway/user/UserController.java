package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UserPatchDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    // GetMapping  public Collection<UserDto> getAll()
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос на получение списка пользователей");
        return userClient.getUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@Positive @PathVariable("id") long id) {
        log.info("Запрос на получения пользовантеля по ID {}", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Запрос на создание  пользователя {}", newUserDto);
        return userClient.createUser(newUserDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Positive @PathVariable("id") long id,
                                         @Valid @RequestBody UserPatchDto userPatchDto) {
        log.info("Запрос на обновление пользвателя {}", id);
        return userClient.updateUser(id, userPatchDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("id") long id) {
        log.info("Запрос на удаление пользователя {}", id);
        return userClient.deleteUser(id);
    }

}