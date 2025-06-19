package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOwnerRequestDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static ru.practicum.shareit.booking.BookingCheck.*;
import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.item.ItemCheck.*;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
    @Override
    public BookingDto createBooking(long userId, NewBookingDto newBookingDto) {
        isStartEndValid(newBookingDto); // валидация старта и конца брони
        // проверяем существоание такого пользователя
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        // проверяем существование вещи
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %s не найдена", newBookingDto.getItemId())));
        // проверка доступности вещи для брони
        isItemAvailable(item);
        return toBookingDto(bookingRepository.save(toBooking(newBookingDto, booker, item)));
    }

    // Подтверждение или отклонение запроса на бронирование.
    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        // Может быть выполнено только владельцем вещи.
        isOwner(userId, booking.getItem().getOwner());
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    // Получение данных о конкретном бронировании (включая его статус).
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования  с ID %s не найден", bookingId)));
        // Может быть выполнено либо автором бронирования, либо владельцем вещи,к которой относится бронирование.
        isBookerOrOwner(userId, booking.getBooker().getId(), booking.getItem().getOwner());
        return toBookingDto(booking);
    }

    // Получение списка всех бронирований текущего пользователя.
    // Эндпоинт — `GET /bookings?state={state}`.
    // Параметр `state` необязательный и по умолчанию равен ALL  -все
    // Также он может принимать значения^
    // CURRENT - «текущие»
    // PAST - «завершённые»
    // FUTURE - «будущие»
    // WAITING - «ожидающие подтверждения»
    // REJECTED - «отклонённые»
    // Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @Override
    public Collection<BookingOwnerRequestDto> findAllBookings(long userId, State state) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
       if (state.equals(State.ALL)) {
           return bookingRepository.findByBooker_IdOrderByStartAsc(userId).stream()
                   .map(BookingMapper::toBookingOwnerRequestDto)
                   .toList();
       } else {
           return bookingRepository.findByBooker_IdOrderByStartAsc(userId).stream()
                   .map(BookingMapper::toBookingOwnerRequestDto)
                   .filter(bookingOwnerRequestDto -> bookingOwnerRequestDto.getStatus().equals(state))
                   .toList();
       }

    }

    // Получение списка бронирований для всех вещей текущего пользователя.
    // Эндпоинт — `GET /bookings/owner?state={state}`. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    // Работа параметра `state` аналогична его работе в предыдущем сценарии.
    @Override
    public Collection<BookingOwnerRequestDto> findAllOwnerBooking(long userId, State state) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        if (state.equals(State.ALL)) {
            return bookingRepository.findByBooker_IdOrderByStartAsc(userId).stream()
                    .filter(booking -> booking.getItem().getOwner() == userId)
                    .map(BookingMapper::toBookingOwnerRequestDto)
                    .toList();
        } else {
            return bookingRepository.findByBooker_IdOrderByStartAsc(userId).stream()
                    .filter(booking -> booking.getItem().getOwner() == userId)
                    .map(BookingMapper::toBookingOwnerRequestDto)
                    .filter(bookingOwnerRequestDto -> bookingOwnerRequestDto.getStatus().equals(state))
                    .toList();
        }
    }

}
