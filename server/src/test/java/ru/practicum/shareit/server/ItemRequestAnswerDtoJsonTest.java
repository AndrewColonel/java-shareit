package ru.practicum.shareit.server;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.server.request.dto.ItemAnswerDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestAnswerDtoJsonTest {

    // Инициализируем JacksonTester для ItemRequestAnswerDto
    private final JacksonTester<ItemRequestAnswerDto> jsonTester;

    @Test
    void testSerializeItemRequestAnswerDto() throws Exception {
        // Создаем вложенный ItemAnswerDto
        ItemAnswerDto itemAnswerDto = ItemAnswerDto.builder()
                .id(1L)
                .name("Drill")
                .build();

        // Создаем ItemRequestAnswerDto с тестовыми данными
        ItemRequestAnswerDto dto = ItemRequestAnswerDto.builder()
                .id(100L)
                .description("Need a drill")
                .created(LocalDateTime.of(2025, 4, 5, 10, 0))
                .items(List.of(itemAnswerDto))
                .build();

        // Сериализуем в JSON
        var jsonContent = jsonTester.write(dto);

        // Проверяем JSON-структуру
        jsonContent.assertThat()
                .hasJsonPath("$.id", 100)
                .hasJsonPath("$.description", "Need a drill")
                .hasJsonPath("$.created", "2025-04-05T10:00:00")
                .hasJsonPath("$.items[0].id", 1)
                .hasJsonPath("$.items[0].name", "Drill");
    }

    @Test
    void testDeserializeItemRequestAnswerDto() throws Exception {
        // Тестовый JSON с вложенным ItemAnswerDto
        String json = "{\n"
                + "    \"id\": 100,\n"
                + "    \"description\": \"Need a drill\",\n"
                + "    \"created\": \"2025-04-05T10:00:00\",\n"
                + "    \"items\": [\n"
                + "        {\n"
                + "            \"id\": 1,\n"
                + "            \"name\": \"Drill\"\n"
                + "        }\n"
                + "    ]\n"
                + "}";

        // Десериализуем JSON в объект ItemRequestAnswerDto
        ItemRequestAnswerDto dto = jsonTester.parseObject(json);

        // Проверяем поля
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.parse("2025-04-05T10:00:00"));

        // Проверяем вложенные объекты
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void testDeserializeWithNullItems() throws Exception {
        // Тестовый JSON с null в поле items
        String json = "{\n"
                + "    \"id\": 100,\n"
                + "    \"description\": \"Need a drill\",\n"
                + "    \"created\": \"2025-04-05T10:00:00\",\n"
                + "    \"items\": null\n"
                + "}";

        // Десериализуем JSON
        ItemRequestAnswerDto dto = jsonTester.parseObject(json);

        // Проверяем, что items == null
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void testDeserializeWithMissingItemsField() throws Exception {
        // Тестовый JSON без поля items
        String json = "{\n"
                + "    \"id\": 100,\n"
                + "    \"description\": \"Need a drill\",\n"
                + "    \"created\": \"2025-04-05T10:00:00\"\n"
                + "}";

        // Десериализуем JSON
        ItemRequestAnswerDto dto = jsonTester.parseObject(json);

        // Проверяем, что items == null
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void testDeserializeWithEmptyItemsList() throws Exception {
        // Тестовый JSON с пустым списком items
        String json = "{\n"
                + "    \"id\": 100,\n"
                + "    \"description\": \"Need a drill\",\n"
                + "    \"created\": \"2025-04-05T10:00:00\",\n"
                + "    \"items\": []\n"
                + "}";

        // Десериализуем JSON
        ItemRequestAnswerDto dto = jsonTester.parseObject(json);

        // Проверяем, что items == пустой список
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }
}