package ru.practicum.shareit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.request.ItemRequestServiceImpl;
import ru.practicum.shareit.server.request.dto.*;
import ru.practicum.shareit.server.request.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTests {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 100L;

    private User user;
    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private Item item;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);

        LocalDateTime now = LocalDateTime.now();

        user = new User();
        user.setId(USER_ID);
        user.setName("Alice");
        user.setEmail("alice@example.com");

//        newItemRequestDto = new NewItemRequestDto("Need a drill", now);

        newItemRequestDto = NewItemRequestDto.builder()
                .description("Need a drill")
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(REQUEST_ID);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestor(user.getId());
        itemRequest.setCreated(now);

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful electric drill");
        item.setAvailable(true);
        item.setRequest(itemRequest);
    }

    @Test
    void testCreateItemRequest_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(USER_ID, newItemRequestDto);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void testCreateItemRequest_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(USER_ID, newItemRequestDto));
    }

    @Test
    void testFindAllByOwnerRequests_success() {
        ItemRequestAnswerDto answerDto = ItemRequestMapper.toItemRequestAnswerDto(itemRequest);
        answerDto.setItems(List.of(ItemMapper.toItemAnswer(item)));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorOrderByCreatedAsc(USER_ID)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequest_idIn(List.of(REQUEST_ID))).thenReturn(List.of(item));

        List<ItemRequestAnswerDto> result = itemRequestService.findAllByOwnerRequests(USER_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void testFindAllRequests_success() {
        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setId(2L);
        otherRequest.setDescription("Another request");
        otherRequest.setRequestor(2L);
        otherRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorNotOrderByCreatedAsc(USER_ID)).thenReturn(List.of(otherRequest));

        List<ItemRequestDto> result = itemRequestService.findAllRequests(USER_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetRequestById_success() {
        ItemAnswerDto answerDto = ItemMapper.toItemAnswer(item);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest_id(REQUEST_ID)).thenReturn(List.of(item));

        ItemRequestAnswerDto result = itemRequestService.getRequestById(USER_ID, REQUEST_ID);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().getFirst().getName());
    }

    @Test
    void testGetRequestById_requestNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(USER_ID, REQUEST_ID));
    }
}