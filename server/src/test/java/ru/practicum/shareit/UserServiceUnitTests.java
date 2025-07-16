package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.UserServiceImpl;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    private User user;
    private NewUserDto newUserDto;
    private List<User> userList;
    private UserService mockUserServiceImpl;

    @BeforeEach
    void setup() {
        newUserDto = NewUserDto.builder()
                .id(1)
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();

        user = new User();
        user.setId(1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");
        userList = List.of(user);

        mockUserServiceImpl = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
//        UserService mockUserServiceImpl = new UserServiceImpl(userRepository);
        UserDto mockUserDto = mockUserServiceImpl.createUser(newUserDto);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
    }

    @Test
    void testFindAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(userList);
//        UserService mockUserServiceImpl = new UserServiceImpl(userRepository);
        assertEquals(1, mockUserServiceImpl.findAllUsers().size());
    }

    @Test
    void testfindUserById() {
//        UserService mockUserServiceImpl = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с ID 1 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(1));
        assertEquals("Пользователь с ID 1 не найден", exception.getMessage());

    }
}
