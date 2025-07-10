package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

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
    public UserDto getById(@Positive  @PathVariable("id") long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.createUser(newUserDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Positive @PathVariable("id") long id,
                          @Valid @RequestBody UserPatchDto userPatchDto) {
        return userService.updateUser(id, userPatchDto);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@Positive @PathVariable("id") long id) {
        return userService.deleteUser(id);
    }
}
