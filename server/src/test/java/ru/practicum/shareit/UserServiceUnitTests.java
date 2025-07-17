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
import ru.practicum.shareit.server.user.dto.UserPatchDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.Optional;

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
    private Optional<User> maybeUser;
    private UserPatchDto userPatchDto;

    @BeforeEach
    void setup() {
        newUserDto = NewUserDto.builder()
                .id(1)
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        userPatchDto = UserPatchDto.builder()
                .email("Genesis22@gmail.com")
                .name("Ms. Cesar Funk")
                .build();
        user = new User();
        user.setId(1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");
        userList = List.of(user);
        maybeUser = Optional.of(user);
        mockUserServiceImpl = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        UserDto mockUserDto = mockUserServiceImpl.createUser(newUserDto);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void testFindAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(userList);
        assertEquals(1, mockUserServiceImpl.findAllUsers().size());
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void testfindUserById() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(maybeUser);
        assertEquals("Genesis22@gmail.com", mockUserServiceImpl.findUserById(1L).getEmail());
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(2))
                .findById(anyLong());
    }

    @Test
    void testUpdateUser() {
        Mockito
                .when(userRepository.findById(any()))
                .thenReturn(maybeUser);
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        UserDto mockUserDto = mockUserServiceImpl.updateUser(1L, userPatchDto);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void testDeleteUser() {
        Mockito
                .when(userRepository.findById(any()))
                .thenReturn(maybeUser);
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mockUserServiceImpl.findUserById(2));
        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        UserDto mockUserDto = mockUserServiceImpl.deleteUser(1L);
        assertEquals("Ms. Cesar Funk", mockUserDto.getName());
        Mockito.verify(userRepository, Mockito.times(1))
                .delete(any());
    }

}
