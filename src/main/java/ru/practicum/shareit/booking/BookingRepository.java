package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartAsc(long bookerId);

    // здесь нужен запрос не ByBooker_Id, а ByItem_Owner_Id

    List<Booking> findByItem_IdAndEndIsBeforeOrderByEndAsc(Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdAndStartIsAfterOrderByEndAsc(Long itemId, LocalDateTime start);

    Optional<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

}
