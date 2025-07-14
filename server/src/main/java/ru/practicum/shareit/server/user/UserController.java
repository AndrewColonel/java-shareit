package ru.practicum.shareit.server.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserPatchDto;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody NewUserDto newUserDto) {
        return userService.createUser(newUserDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long id,
                          @RequestBody UserPatchDto userPatchDto) {
        return userService.updateUser(id, userPatchDto);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable("id") long id) {
        return userService.deleteUser(id);
    }
}
