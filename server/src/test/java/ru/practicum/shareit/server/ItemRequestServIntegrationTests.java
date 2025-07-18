package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.request.ItemRequestService;
import ru.practicum.shareit.server.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServIntegrationTests {

    private final EntityManager em;

    private final ItemRequestService itemRequestService;

    private final UserRepository userRepository;
    private static User user;
    private static User owner;
    private static NewItemRequestDto newItemRequestDto;
    private static NewItemRequestDto otherRequest;
    private static NewUserDto newUserDto1;
    private static NewUserDto newUserDto2;
    private static NewItemDto newItemDto;
    private static LocalDateTime now;

    @BeforeAll
    static void modelSetup() {
        now = LocalDateTime.now();
        newUserDto1 = NewUserDto.builder()
                .name("Ms. Cesar Funk")
                .email("Genesis22@gmail.com")
                .build();
        newUserDto2 = NewUserDto.builder()
                .name("Billie Ryan")
                .email("Citlalli59@hotmail.com")
                .build();

        newItemDto = NewItemDto.builder()
                .name("Saw")
                .description("Hand saw")
                .available(true)
                .owner(1)
                .build();

        newItemRequestDto = NewItemRequestDto.builder()
                .description("Need a drill")
                .build();

        otherRequest = NewItemRequestDto.builder()
                .description("Another request")
                .build();
    }

    @BeforeEach
    void setup() {
        user = userRepository.save(UserMapper.toUser(newUserDto1));
        owner = userRepository.save(UserMapper.toUser(newUserDto2));
    }

    @Test
    void testCreateItemRequest_success() {
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);

        TypedQuery<ItemRequest> query =
                em.createQuery("Select ir from ItemRequest ir where ir.description = :description",
                        ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertNotNull(itemRequestDto);
        assertNotEquals("Need a hammer", itemRequestDto.getDescription());
        assertNotNull(itemRequestDto.getId());
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(999L, newItemRequestDto));
    }

    @Test
    void testFindAllByOwnerRequests_success() {

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto);
        List<ItemRequestAnswerDto> result = itemRequestService.findAllByOwnerRequests(user.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getItems());

        for (ItemRequestDto itemRequestDto1 : itemRequestDtoList) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequestDto1.getDescription())),
                    hasProperty("created", equalTo(itemRequestDto1.getCreated()))
            )));
        }

    }

    @Test
    void testFindAllRequests_unsuccess() {

        ItemRequestDto itemRequestDto1 = itemRequestService.createItemRequest(user.getId(), otherRequest);
        ItemRequestDto itemRequestDto2 = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        List<ItemRequestDto> result = itemRequestService.findAllRequests(owner.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetRequestById_success() {
        ItemRequestDto itemRequestDto2 = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        ItemRequestAnswerDto result = itemRequestService.getRequestById(user.getId(), itemRequestDto2.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto2.getId(), result.getId());
        assertNotNull(result.getItems());

    }

    @Test
    void testGetRequestById_requestNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(user.getId(), 999L));
    }

}