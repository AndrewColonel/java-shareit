package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.BookingServiceImpl;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.booking.BookingMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 100L;
    private static final long BOOKING_ID = 200L;

    private User user;
    private Item item;
    private NewBookingDto newBookingDto;
    private Booking booking;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {

        now = LocalDateTime.now().plusDays(1); // чтобы start был в будущем

        user = new User();
        user.setId(USER_ID);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        item = new Item();
        item.setId(ITEM_ID);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user.getId());

        newBookingDto = NewBookingDto.builder()
                .itemId(ITEM_ID)
                .start(now)
                .end(now.plusHours(2))
                .build();
        newBookingDto.setItemId(ITEM_ID);
        newBookingDto.setStart(now);
        newBookingDto.setEnd(now.plusHours(2));

        booking = BookingMapper.toBooking(newBookingDto, user, item);
        booking.setId(BOOKING_ID);
        booking.setStatus(Status.WAITING);
    }

    @Test
    void testCreateBooking_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(USER_ID, newBookingDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, booking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(USER_ID, newBookingDto));
    }

    @Test
    void testCreateBooking_itemNotFound_throwsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(USER_ID, newBookingDto));
    }

    @Test
    void testUpdateBooking_success_approved() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(USER_ID, BOOKING_ID, true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void testUpdateBooking_alreadyApproved_cannotChange() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(USER_ID, BOOKING_ID, false));
    }

    @Test
    void testGetBookingById_success() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(USER_ID, BOOKING_ID);

        assertNotNull(result);
        assertEquals(BOOKING_ID, result.getId());
    }

    @Test
    void testGetBookingById_notFound_throwsNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(USER_ID, BOOKING_ID));
    }
}