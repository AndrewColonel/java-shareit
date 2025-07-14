package ru.practicum.shareit.gateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
}
