package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserPatchDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private static NewUserDto newUserDto1;
    private static NewUserDto newUserDto2;
    private static UserPatchDto userPatchDto;

    @BeforeAll
    static void setup() {
        newUserDto1 = NewUserDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        newUserDto2 = NewUserDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();
        userPatchDto = UserPatchDto.builder()
                .name("Judith Hahn")
                .email("Ila_Friesen@hotmail.com")
                .build();
    }

    @Test
    void createUserTest() {
        UserDto userDto = userService.createUser(newUserDto1);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUserDto1.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto1.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto1.getEmail()));
        assertThat(userDto.getName(), equalTo(newUserDto1.getName()));
        assertThat(userDto.getEmail(), equalTo(newUserDto1.getEmail()));
    }

    @Test
    void findUsreByIdTest() {
        userService.createUser(newUserDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUserDto2.getEmail())
                .getSingleResult();

        UserDto userDto = userService.findUserById(user.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto2.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto2.getEmail()));
        assertThat(userDto.getName(), equalTo(newUserDto2.getName()));
        assertThat(userDto.getEmail(), equalTo(newUserDto2.getEmail()));
    }

    @Test
    void findAllUsersTest() {
        List<NewUserDto> sourceUsers = List.of(newUserDto1, newUserDto2);

        for (NewUserDto newUserDto : sourceUsers) {
            User entity = UserMapper.toUser(newUserDto);
            em.persist(entity);
        }
        em.flush();

        // when
        Collection<UserDto> targetUsers = userService.findAllUsers();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (NewUserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }

    }

    @Test
    void updateUserTest() {
        userService.createUser(newUserDto1);
        TypedQuery<User> queryBefore = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userBefore = queryBefore.setParameter("email", newUserDto1.getEmail())
                .getSingleResult();
        UserDto userDto = userService.updateUser(userBefore.getId(), userPatchDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userPatchDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userPatchDto.getName()));
        assertThat(user.getEmail(), equalTo(userPatchDto.getEmail()));
        assertThat(userDto.getName(), equalTo(userPatchDto.getName()));
        assertThat(userDto.getEmail(), equalTo(userPatchDto.getEmail()));
    }

    @Test
    void deleteUserTest() {
        userService.createUser(newUserDto2);
        TypedQuery<User> queryBefore = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userBefore = queryBefore.setParameter("email", newUserDto2.getEmail())
                .getSingleResult();

        UserDto userDto = userService.deleteUser(userBefore.getId());
        assertThat(userBefore.getId(), notNullValue());
        assertThat(userBefore.getName(), equalTo(userDto.getName()));
        assertThat(userBefore.getEmail(), equalTo(userDto.getEmail()));
    }


}
