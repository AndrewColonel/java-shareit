package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
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


@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTests {
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

    private Item item;
    private NewItemDto newItemDto;
    private ItemService itemServiceImpl;
    private Optional<Item> maybeItem;
    private ItemPatchDto itemPatchDto;

    private User user;
    private Optional<User> maybeUser;

    private Comment comment;
    private CommentDto commentDto;

    private Booking booking;
    private Optional<Booking> maybeBooking;

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
        maybeItem = Optional.of(item);
        user = new User();
        user.setId(1);
        user.setName("Ms. Cesar Funk");
        user.setEmail("Genesis22@gmail.com");

        itemPatchDto = ItemPatchDto.builder()
                .name("Ms. Cesar Funk")
                .description("2MG5XYEtjFlTTOweF1NRd4PrTgjWI7XRWWbSMw8DbEDEjWdWhh")
                .build();
        maybeUser = Optional.of(user);

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

        maybeBooking = Optional.of(booking);

        itemServiceImpl = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
    }

    @Test
    void testCreateItem() {
        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(maybeUser);
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createItem(2L, newItemDto));

        ItemDto itemDto = (itemServiceImpl.createItem(1L, newItemDto));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertEquals("Пользователь с ID 2 не найден", exception.getMessage());

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(any());
        Mockito.verify(userRepository, Mockito.times(2))
                .findById(anyLong());
    }

    @Test
    void testUpdateItem() {
        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(maybeUser);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(maybeItem);
        Mockito
                .when(itemRepository.findById(2L))
                .thenThrow(new NotFoundException("Вещь с ID 2 не найдена"));
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exceptionUser = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.updateItem(2L, 2L, itemPatchDto));
        final NotFoundException exceptionItem = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.updateItem(1L, 2L, itemPatchDto));

        ItemDto itemDto = (itemServiceImpl.updateItem(1L, 1L, itemPatchDto));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertEquals("Пользователь с ID 2 не найден", exceptionUser.getMessage());
        assertEquals("Вещь с ID 2 не найдена", exceptionItem.getMessage());
    }

    @Test
    void testCreateComment() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(maybeUser);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(maybeItem);

        Mockito
                .when(bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(anyLong(),
                        anyLong(), any()))
                .thenReturn(maybeBooking);
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito
                .when(itemRepository.findById(2L))
                .thenThrow(new NotFoundException("Вещь с ID 2 не найдена"));
        Mockito
                .when(userRepository.findById(2L))
                .thenThrow(new NotFoundException("Пользователь с ID 2 не найден"));
        final NotFoundException exceptionUser = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createComment(2L, 2L, commentDto));
        final NotFoundException exceptionItem = assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.createComment(1L, 2L, commentDto));


        assertEquals("Пользователь с ID 2 не найден", exceptionUser.getMessage());
        assertEquals("Вещь с ID 2 не найдена", exceptionItem.getMessage());

        CommentDto commentDtoResult = itemServiceImpl.createComment(1L, 1L, commentDto);
        assertThat(commentDtoResult.getId(), equalTo(commentDto.getId()));
        assertThat(commentDtoResult.getCreated(), equalTo(commentDto.getCreated()));
    }

}
