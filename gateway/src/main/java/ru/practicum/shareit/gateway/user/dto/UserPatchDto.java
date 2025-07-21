package ru.practicum.shareit.gateway.user.dto;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    // имя или логин пользователя
    private String name;
    // адрес электронной почты (учтите, что два пользователя не могут
    // иметь одинаковый адрес электронной почты)
    @Email
    private String email;
}
