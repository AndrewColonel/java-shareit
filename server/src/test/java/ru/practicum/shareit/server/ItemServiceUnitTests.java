package ru.practicum.shareit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.*;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTests {

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private static final Long USER_ID1 = 1L;
    private static final Long USER_ID2 = 2L;
    private static final Long USER_ID3 = 3L;
    private static final Long ITEM_ID1 = 1L;
    private static final Long ITEM_ID2 = 2L;


    private Item item;
    private NewItemDto newItemDto;
//    private ItemService itemServiceImpl;
    private ItemPatchDto itemPatchDto;

    private User user;
    private User owner;

    private Comment comment;
    private CommentDto commentDto;

    private Booking booking;

    private LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();

        item = new Item();
        item.setId(ITEM_ID1);
        item.setName("nh3ko8vqPe");
        item.setOwner(USER_ID3);
        item.setDescription("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh");
        item.setAvailable(true);

        newItemDto = NewItemDto.builder()
                .name("nh3ko8vqPe")
                .available(true)
                .description("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh")
                .build();

        user = new User();
        user.setId(USER_ID1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");

        owner = new User();
        owner.setId(USER_ID3);
        owner.setName("Bob");
        owner.setEmail("bob@example.com");

        itemPatchDto = ItemPatchDto.builder()
                .name("Ms. Cesar Funk")
                .description("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh")
                .build();

        comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setText("ZdVZzEyamypLO4QlDdVQmZZrVuW5MLdr9uTlAifk8pAtTk2XWI");
        comment.setCreated(LocalDateTime.parse("2025-07-17T12:43:23.904875291"));
        comment.setAuthor(user);

        commentDto = CommentDto.builder()
                .id(1L)
                .authorName("Ms. Cesar Funk")
                .text("ZdVZzEyamypLO4QlDdVQmZZrVuW5MLdr9uTlAifk8pAtTk2XWI")
                .created(LocalDateTime.parse("2025-07-17T12:43:23.904875291"))
                .item(1L)
                .build();

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.parse("2025-07-18T13:13:16"));
        booking.setEnd(LocalDateTime.parse("2025-07-19T13:13:16"));
        booking.setBooker(user);

        itemServiceImpl = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
    }

    @Test
    void testCreateItem_success() {

        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userRepository.findById(USER_ID1))
                .thenReturn(Optional.of(user));
        when(userRepository.findById(USER_ID2))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createItem(USER_ID2, newItemDto));

        ItemDto itemDto = (itemServiceImpl.createItem(USER_ID1, newItemDto));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        verify(itemRepository, times(1))
                .save(any());
        verify(userRepository, times(2))
                .findById(anyLong());
    }

    @Test
    void testCreateItem_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemServiceImpl.createItem(999L, newItemDto));
    }



    @Test
    void testUpdateItem_success() {
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userRepository.findById(USER_ID1))
                .thenReturn(Optional.of(user));
        when(userRepository.findById(USER_ID3))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID1))
                .thenReturn(Optional.of(item));
        when(itemRepository.findById(ITEM_ID2))
                .thenThrow(new NotFoundException("Вещь с ID 2 не найдена"));
        when(userRepository.findById(USER_ID2))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exceptionUser = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.updateItem(USER_ID2, ITEM_ID2, itemPatchDto));
        final NotFoundException exceptionItem = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.updateItem(USER_ID1, ITEM_ID2, itemPatchDto));

        ItemDto itemDto = (itemServiceImpl.updateItem(USER_ID3, ITEM_ID1, itemPatchDto));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertEquals("Пользователь с ID 2 не найден", exceptionUser.getMessage());
        assertEquals("Вещь с ID 2 не найдена", exceptionItem.getMessage());
    }

    @Test
    void testUpdateItem_notOwner_throwsNotFoundException() {
        itemPatchDto.setName("Improved Drill");
        when(userRepository.findById(USER_ID1)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ITEM_ID1)).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () ->
                itemServiceImpl.updateItem(USER_ID1, ITEM_ID1, itemPatchDto));
    }

    @Test
    void testFindAllItems_success() {
        List<Item> itemList = List.of(item);
        List<Long> itemIdList = List.of(item.getId());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwner(owner.getId())).thenReturn(itemList);
        when(bookingRepository.findByItem_IdInOrderByStartAsc(itemIdList)).thenReturn(List.of());
        when(commentRepository.findByItem_IdInOrderByCreatedAsc(itemIdList)).thenReturn(List.of());

        Collection<ItemOwnerViewingDto> result = itemServiceImpl.findAllItems(owner.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void testSearchItems_success() {
        when(itemRepository.search("Drill")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemServiceImpl.searchItems("Drill");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testSearchItems_emptyQuery_returnsEmpty() {
        Collection<ItemDto> result = itemServiceImpl.searchItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateComment() {
        when(userRepository.findById(USER_ID1))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(ITEM_ID1))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(anyLong(),
                anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        when(itemRepository.findById(ITEM_ID2))
                .thenThrow(new NotFoundException("Вещь с ID 2 не найдена"));
        when(userRepository.findById(USER_ID2))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exceptionUser = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createComment(2L, 2L, commentDto));
        final NotFoundException exceptionItem = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createComment(1L, 2L, commentDto));


        assertEquals("Пользователь с ID 2 не найден", exceptionUser.getMessage());
        assertEquals("Вещь с ID 2 не найдена", exceptionItem.getMessage());

        CommentDto commentDtoResult = itemServiceImpl.createComment(USER_ID1, ITEM_ID1, commentDto);
        assertThat(commentDtoResult.getId(), equalTo(commentDto.getId()));
        assertThat(commentDtoResult.getCreated(), equalTo(commentDto.getCreated()));
    }

}
