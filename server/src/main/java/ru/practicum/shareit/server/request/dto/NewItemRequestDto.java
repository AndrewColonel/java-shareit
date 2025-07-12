package ru.practicum.shareit.server.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
}
