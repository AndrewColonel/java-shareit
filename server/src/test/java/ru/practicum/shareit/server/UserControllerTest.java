package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.user.UserController;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserPatchDto;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long USER_ID = 1L;

    private static UserDto userDto;
    private static NewUserDto newUserDto;
    private static UserPatchDto userPatchDto;

    @BeforeAll
    static void setup() {
        userDto = UserDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        newUserDto = NewUserDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();
        userPatchDto = UserPatchDto.builder()
                .name("Judith Hahn")
                .email("Ila_Friesen@hotmail.com")
                .build();
    }

    @Test
    void testGetAllUsers_success() throws Exception {


        when(userService.findAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testGetUserById_success() throws Exception {
        when(userService.findUserById(USER_ID)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));

        verify(userService, times(1)).findUserById(USER_ID);
    }

    @Test
    void testGetUserById_notFound_throwsNotFoundException() throws Exception {
        when(userService.findUserById(USER_ID))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/{id}", USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findUserById(USER_ID);
    }

    @Test
    void testCreateUser_success() throws Exception {

        when(userService.createUser(newUserDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createUser(newUserDto);
    }

    @Test
    void testCreateUser_validationError_throwsValidationException() throws Exception {
         newUserDto.setEmail("invalid-email"); // Невалидный email

        when(userService.createUser(newUserDto))
                .thenThrow(new ValidationException("Email не валиден"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isBadRequest());


        verify(userService, times(1)).createUser(newUserDto);
    }

    // --- PATCH /users/{id} ---

    @Test
    void testUpdateUser_success() throws Exception {


        UserDto updatedUser = UserDto.builder()
                .id(USER_ID)
                .name("Judith Hahn")
                .email("Ila_Friesen@hotmail.com")
                .build();

        when(userService.updateUser(USER_ID, userPatchDto)).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPatchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedUser.getName()));

        verify(userService, times(1)).updateUser(USER_ID, userPatchDto);
    }

    @Test
    void testUpdateUser_notFound_throwsNotFoundException() throws Exception {

        when(userService.updateUser(USER_ID, userPatchDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(patch("/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPatchDto)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(USER_ID, userPatchDto);
    }

    // --- DELETE /users/{id} ---

    @Test
    void testDeleteUser_success() throws Exception {
        UserDto deletedUser = UserDto.builder().build();
        deletedUser.setId(USER_ID);
        deletedUser.setName("Alice");
        deletedUser.setEmail("alice@example.com");
        when(userService.deleteUser(USER_ID)).thenReturn(deletedUser);
        mockMvc.perform(delete("/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID));
        verify(userService, times(1)).deleteUser(USER_ID);
    }

    @Test
    void testDeleteUser_notFound_throwsNotFoundException() throws Exception {
        when(userService.deleteUser(USER_ID))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(delete("/users/{id}", USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(USER_ID);
    }
}