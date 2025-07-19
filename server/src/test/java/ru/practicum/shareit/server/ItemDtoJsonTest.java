package ru.practicum.shareit.server;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {

    private final JacksonTester<ItemDto> jsonTester;

    @Test
    void testSerializeItemDto() throws IOException {
        // Создаем объект ItemDto с тестовыми данными
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful electric drill")
                .available(true)
                .owner(100L)
                .requestId(1000L)
                .build();

        // Сериализуем в JSON
        var jsonContent = jsonTester.write(itemDto);

        // Проверяем JSON-структуру
        jsonContent.assertThat()
                .hasJsonPath("$.id", 1)
                .hasJsonPath("$.name", "Drill")
                .hasJsonPath("$.description", "Powerful electric drill")
                .hasJsonPath("$.available", true)
                .hasJsonPath("$.owner", 100)
                .hasJsonPath("$.requestId", 1000);
    }

    @Test
    void testDeserializeItemDto() throws IOException {
        // Тестовый JSON
        String json = """
        {
            "id": 1,
            "name": "Drill",
            "description": "Powerful electric drill",
            "available": true,
            "owner": 100,
            "requestId": 1000
        }
        """;

        // Десериализуем JSON в объект ItemDto
        ItemDto itemDto = jsonTester.parseObject(json);

        // Проверяем поля объекта
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful electric drill");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getOwner()).isEqualTo(100L);
        assertThat(itemDto.getRequestId()).isEqualTo(1000L);
    }

    @Test
    void testDeserializeItemDtoWithNullRequestId() throws IOException {
        // Тестовый JSON с null в requestId
        String json = """
        {
            "id": 1,
            "name": "Drill",
            "description": "Powerful electric drill",
            "available": true,
            "owner": 100
        }
        """;

        // Десериализуем JSON в объект ItemDto
        ItemDto itemDto = jsonTester.parseObject(json);

        // Проверяем, что requestId == null
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void testDeserializeItemDtoWithNullAvailable() throws IOException {
        // Тестовый JSON с null в available
        String json = """
        {
            "id": 1,
            "name": "Drill",
            "description": "Powerful electric drill",
            "available": null,
            "owner": 100
        }
        """;

        // Десериализуем JSON в объект ItemDto
        ItemDto itemDto = jsonTester.parseObject(json);

        // Проверяем, что available == null
        assertThat(itemDto.getAvailable()).isNull();
    }
}
