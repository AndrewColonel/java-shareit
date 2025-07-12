package ru.practicum.shareit.gateway.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;


    // GetMapping  public Collection<UserDto> getAll()


    // GetMapping("/{id}") public UserDto getById(@Positive  @PathVariable("id") long id)

    // PostMapping public UserDto create(@Valid @RequestBody NewUserDto newUserDto)

    // PatchMapping("/{id}") public UserDto update(@Positive @PathVariable("id") long id,
    // @Valid @RequestBody UserPatchDto userPatchDto)

    // DeleteMapping("/{id}") public UserDto delete(@Positive @PathVariable("id") long id) }

}