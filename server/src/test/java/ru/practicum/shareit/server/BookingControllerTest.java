package ru.practicum.shareit.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long USER_ID = 1L;
    private static final long BOOKING_ID = 100L;

    // --- POST /bookings ---

    @Test
    void testCreateBooking_success() throws Exception {
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(1L);
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(BOOKING_ID);
        bookingDto.setStatus(Status.WAITING);

        when(bookingService.createBooking(USER_ID, newBookingDto)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).createBooking(USER_ID, newBookingDto);
    }

    @Test
    void testCreateBooking_userNotFound_throwsNotFoundException() throws Exception {
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(1L);
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.createBooking(USER_ID, newBookingDto))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));

        verify(bookingService, times(1)).createBooking(USER_ID, newBookingDto);
    }

    // --- PATCH /bookings/{bookingId} ---

    @Test
    void testUpdateBooking_success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(BOOKING_ID);
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.updateBooking(USER_ID, BOOKING_ID, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).updateBooking(USER_ID, BOOKING_ID, true);
    }

    @Test
    void testUpdateBooking_notOwner_throwsNotFoundException() throws Exception {
        when(bookingService.updateBooking(USER_ID, BOOKING_ID, true))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Запрос не найден"));

        verify(bookingService, times(1)).updateBooking(USER_ID, BOOKING_ID, true);
    }

    // --- GET /bookings/{bookingId} ---

    @Test
    void testGetBookingById_success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(BOOKING_ID);
        bookingDto.setStatus(Status.WAITING);

        when(bookingService.getBookingById(USER_ID, BOOKING_ID)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBookingById(USER_ID, BOOKING_ID);
    }

    @Test
    void testGetBookingById_notFound_throwsNotFoundException() throws Exception {
        when(bookingService.getBookingById(USER_ID, BOOKING_ID))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Бронирование не найдено"));

        verify(bookingService, times(1)).getBookingById(USER_ID, BOOKING_ID);
    }

    // --- GET /bookings ---

    @Test
    void testFindAllBookings_success() throws Exception {
        BookingStateRequestDto stateDto = new BookingStateRequestDto();
        stateDto.setId(BOOKING_ID);
        stateDto.setStatus(State.ALL);

        when(bookingService.findAllBookings(USER_ID, "ALL")).thenReturn(List.of(stateDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("ALL"));

        verify(bookingService, times(1)).findAllBookings(USER_ID, "ALL");
    }

    @Test
    void testFindAllBookings_invalidState_throwsValidationException() throws Exception {
        when(bookingService.findAllBookings(USER_ID, "INVALID"))
                .thenThrow(new ValidationException("Неверный статус"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Неверный статус"));

        verify(bookingService, times(1)).findAllBookings(USER_ID, "INVALID");
    }

    // --- GET /bookings/owner ---

    @Test
    void testFindAllOwnerBookings_success() throws Exception {
        BookingStateRequestDto stateDto = new BookingStateRequestDto();
        stateDto.setId(BOOKING_ID);
        stateDto.setStatus(State.ALL);

        when(bookingService.findAllOwnerBooking(USER_ID, "ALL")).thenReturn(List.of(stateDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("ALL"));

        verify(bookingService, times(1)).findAllOwnerBooking(USER_ID, "ALL");
    }

    @Test
    void testFindAllOwnerBookings_invalidState_throwsValidationException() throws Exception {
        when(bookingService.findAllOwnerBooking(USER_ID, "INVALID"))
                .thenThrow(new ValidationException("Неверный статус"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Неверный статус"));

        verify(bookingService, times(1)).findAllOwnerBooking(USER_ID, "INVALID");
    }
}