package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemAnswer {
    private long id;
    private String name;
    private long owner;
}
