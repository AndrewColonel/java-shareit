package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    @NotNull
    @Size(min = 1, max = 512)
    private String text;
    private long item;
    private String authorName;
    private LocalDateTime created;
}
