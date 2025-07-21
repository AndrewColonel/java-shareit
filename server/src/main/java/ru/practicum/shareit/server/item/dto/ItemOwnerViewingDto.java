package ru.practicum.shareit.server.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemOwnerViewingDto {
    private String name;
    private String description;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<Long> comments;

}