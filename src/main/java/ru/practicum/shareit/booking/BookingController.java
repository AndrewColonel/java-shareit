package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Эндпоинт — `POST /bookings` После создания запрос находится в статусе
    // `WAITING` — «ожидает подтверждения».
    @PostMapping
    public BookingDto create(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    // Эндпоинт — `PATCH /bookings/{bookingId}?approved={approved}`,
    // параметр `approved` может принимать значения `true` или `false`.
    @PatchMapping("/{bookingId}")
    public BookingDto update(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                             @Positive @PathVariable("bookingId") long bookingId,
                             @RequestParam(name = "approved", required = true) boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    // Эндпоинт — `GET /bookings/{bookingId}`.
    @GetMapping("/{bookingId}")
    public BookingDto getById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                              @Positive @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    //- Получение списка всех бронирований текущего пользователя.
    // Эндпоинт — `GET /bookings?state={state}`.
    @GetMapping
    public Collection<BookingDto> findAll(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @RequestParam(name = "state", required = false,
                                                  defaultValue = "ALL") String state) {
        return bookingService.findAllBookings(userId, state);
    }

    //- Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Positive @RequestParam(name = "state", required = false,
                                                         defaultValue = "ALL") String state) {
        return bookingService.findAllOwnerBooking(userId, state);
    }

}
