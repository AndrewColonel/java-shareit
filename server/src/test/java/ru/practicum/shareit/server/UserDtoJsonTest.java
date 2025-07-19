package ru.practicum.shareit.server;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {

    // Инициализируем JacksonTester для UserDto
    private final JacksonTester<UserDto> jsonTester ;

    @Test
    void testSerializeUserDto() throws IOException {
        // Создаем объект UserDto с тестовыми данными
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        // Сериализуем в JSON
        var jsonContent = jsonTester.write(userDto);

        // Проверяем JSON-структуру
        jsonContent.assertThat()
                .hasJsonPath("$.id", 1)
                .hasJsonPath("$.name", "Alice")
                .hasJsonPath("$.email", "alice@example.com");
    }

    @Test
    void testDeserializeUserDto() throws IOException {
        // Тестовый JSON
        String json = """
        {
            "id": 1,
            "name": "Alice",
            "email": "alice@example.com"
        }
        """;

        // Десериализуем JSON в объект UserDto
        UserDto userDto = jsonTester.parseObject(json);

        // Проверяем поля объекта
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Alice");
        assertThat(userDto.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void testDeserializeUserDtoWithNullFields() throws IOException {
        // Тестовый JSON с null значениями
        String json = """
        {
            "id": 1,
            "name": null,
            "email": null
        }
        """;

        // Десериализуем JSON
        UserDto userDto = jsonTester.parseObject(json);

        // Проверяем, что поля name и email равны null
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void testDeserializeUserDtoWithMissingFields() throws IOException {
        // Тестовый JSON без поля name и email
        String json = """
        {
            "id": 1
        }
        """;

        // Десериализуем JSON
        UserDto userDto = jsonTester.parseObject(json);

        // Проверяем, что name и email равны null
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void testDeserializeUserDtoWithEmptyStringFields() throws IOException {
        // Тестовый JSON с пустыми строками
        String json = """
        {
            "id": 1,
            "name": "",
            "email": ""
        }
        """;

        // Десериализуем JSON
        UserDto userDto = jsonTester.parseObject(json);

        // Проверяем, что name и email пустые
        assertThat(userDto.getName()).isEqualTo("");
        assertThat(userDto.getEmail()).isEqualTo("");
    }
}