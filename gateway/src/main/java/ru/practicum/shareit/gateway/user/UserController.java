package ru.practicum.shareit.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
        return null;
    }

    // GetMapping("/{id}") public UserDto getById(@Positive  @PathVariable("id") long id)

    // PostMapping public UserDto create(@Valid @RequestBody NewUserDto newUserDto)

    // PatchMapping("/{id}") public UserDto update(@Positive @PathVariable("id") long id,
    // @Valid @RequestBody UserPatchDto userPatchDto)

    // DeleteMapping("/{id}") public UserDto delete(@Positive @PathVariable("id") long id) }

}