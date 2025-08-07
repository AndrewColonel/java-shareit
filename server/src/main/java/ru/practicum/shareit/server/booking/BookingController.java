package ru.practicum.shareit.server.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStateRequestDto;
import ru.practicum.shareit.server.booking.dto.GetBookingParam;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Эндпоинт — `POST /bookings` После создания запрос находится в статусе
    // WAITING` — «ожидает подтверждения».
    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody NewBookingDto newBookingDto) {

        return bookingService.createBooking(userId, newBookingDto);
    }

    // Эндпоинт — `PATCH /bookings/{bookingId}?approved={approved}`
    // параметр `approved` может принимать значения `true` или `false`
    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable("bookingId") long bookingId,
                             @RequestParam(name = "approved") boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    // Эндпоинт — `GET /bookings/{bookingId}`.
    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    // Получение списка всех бронирований текущего пользователя.
    // Эндпоинт — `GET /bookings?state={state}`
    // Параметр `state` необязательный и по умолчанию равен `ALL` (англ. «все»).
    @GetMapping
    public Collection<BookingStateRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", required = false,
                                                              defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", required = false,
                                                              defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", required = false,
                                                              defaultValue = "10") Integer size){
        return bookingService.findAllBookings(userId, GetBookingParam.of(state, from, size));
    }

    // Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    @GetMapping("/owner")
    public Collection<BookingStateRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(name = "state", required = false,
                                                                     defaultValue = "ALL") String state) {
        return bookingService.findAllOwnerBooking(userId, state);
    }

}
