package ru.practicum.shareit;

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
import ru.practicum.shareit.server.item.*;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemPatchDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static final Long ITEM_ID1 = 1L;
    private static final Long ITEM_ID2 = 2L;


    private Item item;
    private NewItemDto newItemDto;
//    private ItemService itemServiceImpl;
    private ItemPatchDto itemPatchDto;

    private User user;

    private Comment comment;
    private CommentDto commentDto;

    private Booking booking;

    @BeforeEach
    void setup() {
        item = new Item();
        item.setId(1L);
        item.setName("nh3ko8vqPe");
        item.setOwner(1L);
        item.setDescription("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh");
        item.setAvailable(true);

        newItemDto = NewItemDto.builder()
                .name("nh3ko8vqPe")
                .available(true)
                .description("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh")
                .build();

        user = new User();
        user.setId(1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");

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
    void testCreateItem() {

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
    void testUpdateItem() {
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userRepository.findById(USER_ID1))
                .thenReturn(Optional.of(user));
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

        ItemDto itemDto = (itemServiceImpl.updateItem(USER_ID1, ITEM_ID1, itemPatchDto));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertEquals("Пользователь с ID 2 не найден", exceptionUser.getMessage());
        assertEquals("Вещь с ID 2 не найдена", exceptionItem.getMessage());
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
