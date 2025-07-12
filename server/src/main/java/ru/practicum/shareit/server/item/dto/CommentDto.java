package ru.practicum.shareit.server.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    @NotBlank
    private String text;
    private long item;
    private String authorName;
    private LocalDateTime created;
}
