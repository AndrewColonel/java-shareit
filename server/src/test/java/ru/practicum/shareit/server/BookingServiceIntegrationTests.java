package ru.practicum.shareit.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.BookingService;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.request.ItemRequestService;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
//@Rollback(false)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTests {

    private final EntityManager em;

    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;
    private static User user;
    private static User owner;
    private static NewItemRequestDto newItemRequestDto;
    private static NewItemRequestDto otherRequest;
    private static NewUserDto newUserDto1;
    private static NewUserDto newUserDto2;
    private static NewItemDto newItemDto;
    private static LocalDateTime now;
    private static NewBookingDto newBookingDto;

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

        newBookingDto = NewBookingDto.builder()
                .start(now.minusDays(1))
                .end(now.minusHours(1))
                .build();
        newItemRequestDto = NewItemRequestDto.builder()
                .description("Need a drill")
                .build();
    }

    @BeforeEach
    void setup() {
        user = userRepository.save(UserMapper.toUser(newUserDto1));
        owner = userRepository.save(UserMapper.toUser(newUserDto2));
        ItemDto itemDto = itemService.createItem(owner.getId(), newItemDto);
        newBookingDto.setItemId(itemDto.getId());
    }

    @Test
    void testCreateBooking_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), newItemDto);
//        newBookingDto.setItemId(itemDto.getId());
        BookingDto result = bookingService.createBooking(user.getId(), newBookingDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        assertNotNull(result.getId());
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.status = :status",
                        Booking.class);
        Booking booking = query.setParameter("status", result.getStatus())
                .getSingleResult();
        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getStatus(), equalTo(booking.getStatus()));
    }


    @Test
    void testCreateBooking_userNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(999L, newBookingDto));
    }

    @Test
    void testCreateBooking_itemNotFound_throwsNotFoundException() {
        newBookingDto.setItemId(999L);
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(user.getId(), newBookingDto));
    }

    @Test
    void testUpdateBooking_approved_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), newItemDto);
//        newBookingDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), newBookingDto);
        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertNotNull(updated);
        assertEquals(Status.APPROVED, updated.getStatus());
    }

    @Test
    void testUpdateBooking_rejected_success() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), newItemDto);
//        newBookingDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), newBookingDto);
        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertNotNull(updated);
        assertEquals(Status.REJECTED, updated.getStatus());
    }

    @Test
    void testUpdateBooking_notOwner_throwsNotFoundException() {
//        ItemDto itemDto = itemService.createItem(owner.getId(), newItemDto);
//        newBookingDto.setItemId(itemDto.getId());
        BookingDto booking = bookingService.createBooking(user.getId(), newBookingDto);
        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void testGetBookingById_success() {
        BookingDto booking = bookingService.createBooking(user.getId(), newBookingDto);
        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void testGetBookingById_notFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(user.getId(), 999L));
    }

//    @Test
//    void testFindAllBookings_stateAll_success() {
//        bookingService.createBooking(user.getId(), newBookingDto);
//        Collection<BookingStateRequestDto> result = bookingService.findAllBookings(user.getId(), "ALL");
//
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//    }

//    @Test
//    void testFindAllBookings_stateWaiting_success() {
//        bookingService.createBooking(user.getId(), newBookingDto);
//        List<BookingStateRequestDto> resultBooker =
//                bookingService.findAllBookings(user.getId(), "WAITING").stream().toList();
//
//        assertNotNull(resultBooker);
//        assertFalse(resultBooker.isEmpty());
//        assertEquals(1, resultBooker.size());
//        assertEquals("WAITING", resultBooker.getFirst().getStatus().name());
//    }

    @Test
    void testFindAllOwnerBookings_stateAll_success() {
        bookingService.createBooking(user.getId(), newBookingDto);
        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "ALL").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

//    @Test
//    void testFindAllBookings_stateRejected_success() {
//        BookingDto booking = bookingService.createBooking(user.getId(), newBookingDto);
//        BookingDto updated = bookingService.updateBooking(owner.getId(), booking.getId(), false);
////        bookingService.createBooking(user.getId(), newBookingDto);
//        List<BookingStateRequestDto> resultBooker =
//                bookingService.findAllBookings(user.getId(), "REJECTED").stream().toList();
//
//        assertNotNull(resultBooker);
//        assertFalse(resultBooker.isEmpty());
//        assertEquals(1, resultBooker.size());
//        assertEquals("REJECTED", resultBooker.getFirst().getStatus().name());
//    }

    @Test
    void testFindAllOwnerBookings_stateWaiting_success() {
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), newBookingDto);
//        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), true);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "WAITING").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("WAITING", result.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_stateRejected_success() {
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), newBookingDto);
        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), false);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "REJECTED").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("REJECTED", result.getFirst().getStatus().name());
    }

    @Test
    void testFindAllOwnerBookings_state_success() {
        // findAllOwnerBooking
        newBookingDto.setStart(now.minusDays(1));
        newBookingDto.setEnd(now.plusDays(1));
        BookingDto bookingCurrent = bookingService.createBooking(user.getId(), newBookingDto);
        bookingService.updateBooking(owner.getId(), bookingCurrent.getId(), true);

        List<BookingStateRequestDto> result =
                bookingService.findAllOwnerBooking(owner.getId(), "CURRENT").stream().toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("CURRENT", result.getFirst().getStatus().name());

//        // findAllBookings
//        List<BookingStateRequestDto> resultBookerCurrent =
//                bookingService.findAllBookings(user.getId(), "CURRENT").stream().toList();
//
//        assertNotNull(resultBookerCurrent);
//        assertFalse(resultBookerCurrent.isEmpty());
//        assertEquals(1, resultBookerCurrent.size());
//        assertEquals("CURRENT", resultBookerCurrent.getFirst().getStatus().name());

        // findAllOwnerBooking
        newBookingDto.setStart(now.minusDays(1));
        newBookingDto.setEnd(now.minusHours(1));
        BookingDto bookingPast = bookingService.createBooking(user.getId(), newBookingDto);
        bookingService.updateBooking(owner.getId(), bookingPast.getId(), true);

        List<BookingStateRequestDto> resultPast =
                bookingService.findAllOwnerBooking(owner.getId(), "PAST").stream().toList();

        assertNotNull(resultPast);
        assertFalse(resultPast.isEmpty());
        assertEquals("PAST", resultPast.getFirst().getStatus().name());

//        // findAllBookings
//        List<BookingStateRequestDto> resultBookerPast =
//                bookingService.findAllBookings(user.getId(), "PAST").stream().toList();
//
//        assertNotNull(resultBookerPast);
//        assertFalse(resultBookerPast.isEmpty());
//        assertEquals(1, resultBookerPast.size());
//        assertEquals("PAST", resultBookerPast.getFirst().getStatus().name());

        // findAllOwnerBooking
        newBookingDto.setStart(now.plusDays(1));
        newBookingDto.setEnd(now.plusDays(2));
        BookingDto bookingFuture = bookingService.createBooking(user.getId(), newBookingDto);
        bookingService.updateBooking(owner.getId(), bookingFuture.getId(), true);

        List<BookingStateRequestDto> resultFuture =
                bookingService.findAllOwnerBooking(owner.getId(), "FUTURE").stream().toList();

        assertNotNull(resultFuture);
        assertFalse(resultFuture.isEmpty());
        assertEquals("FUTURE", resultFuture.getFirst().getStatus().name());

//        // findAllBookings
//        List<BookingStateRequestDto> resultBookerFuture =
//                bookingService.findAllBookings(user.getId(), "FUTURE").stream().toList();
//
//        assertNotNull(resultBookerFuture);
//        assertFalse(resultBookerFuture.isEmpty());
//        assertEquals(1, resultBookerFuture.size());
//        assertEquals("FUTURE", resultBookerFuture.getFirst().getStatus().name());

    }


}
