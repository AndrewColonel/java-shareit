package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartAsc(long bookerId);

    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndAsc(Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdAndStartIsAfterOrderByEndAsc(Long itemId, LocalDateTime start);

}
