package ru.practicum.shareit.server;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {

    // Инициализируем JacksonTester для BookingDto
    private final JacksonTester<BookingDto> jsonTester;

    @Test
    void testSerializeBookingDto() throws Exception {
        // Создаем вложенные DTO
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(100L)
                .requestId(1000L)
                .build();

        UserDto userDto = UserDto.builder()
                .id(100L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        // Создаем BookingDto с тестовыми данными
        BookingDto bookingDto = BookingDto.builder()
                .id(200L)
                .start(LocalDateTime.of(2025, 4, 5, 10, 0))
                .end(LocalDateTime.of(2025, 4, 5, 12, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();

        // Сериализуем в JSON
        var jsonContent = jsonTester.write(bookingDto);

        // Проверяем JSON-структуру
        jsonContent.assertThat()
                .hasJsonPath("$.id", 200)
                .hasJsonPath("$.start", "2025-04-05T10:00:00")
                .hasJsonPath("$.end", "2025-04-05T12:00:00")
                .hasJsonPath("$.item.id", 1)
                .hasJsonPath("$.item.name", "Drill")
                .hasJsonPath("$.booker.id", 100)
                .hasJsonPath("$.booker.name", "Alice")
                .hasJsonPath("$.status", "APPROVED");
    }

    @Test
    void testDeserializeBookingDto() throws Exception {
        // Тестовый JSON
        //  задано правило Leading braces { (фигурная скобка на той же строке, что и ключевое слово ....0))))))
        String json = "{\n" +
                "    \"id\": 200,\n" +
                "    \"start\": \"2025-04-05T10:00:00\",\n" +
                "    \"end\": \"2025-04-05T12:00:00\",\n" +
                "    \"item\": {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"Drill\",\n" +
                "        \"description\": \"Powerful drill\",\n" +
                "        \"available\": true,\n" +
                "        \"owner\": 100,\n" +
                "        \"requestId\": 1000\n" +
                "    },\n" +
                "    \"booker\": {\n" +
                "        \"id\": 100,\n" +
                "        \"name\": \"Alice\",\n" +
                "        \"email\": \"alice@example.com\"\n" +
                "    },\n" +
                "    \"status\": \"APPROVED\"\n" +
                "}";

        // Десериализуем JSON в BookingDto
        BookingDto bookingDto = jsonTester.parseObject(json);

        // Проверяем поля
        assertThat(bookingDto.getId()).isEqualTo(200L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.parse("2025-04-05T10:00:00"));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.parse("2025-04-05T12:00:00"));
        assertThat(bookingDto.getStatus()).isEqualTo(Status.APPROVED);

        // Проверяем вложенные DTO
        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(1L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("Drill");

        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(100L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("Alice");
    }

    @Test
    void testDeserializeBookingDtoWithNullFields() throws Exception {
        // JSON с null-полями
        String json = "{"
                + "\"id\": 200,"
                + "\"start\": \"2025-04-05T10:00:00\","
                + "\"end\": \"2025-04-05T12:00:00\","
                + "\"status\": \"WAITING\""
                + "}";

        // Десериализуем JSON в BookingDto
        BookingDto bookingDto = jsonTester.parseObject(json);

        // Проверяем поля
        assertThat(bookingDto.getId()).isEqualTo(200L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.parse("2025-04-05T10:00:00"));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.parse("2025-04-05T12:00:00"));
        assertThat(bookingDto.getStatus()).isEqualTo(Status.WAITING);

        // Проверяем, что вложенные объекты равны null
        assertThat(bookingDto.getItem()).isNull();
        assertThat(bookingDto.getBooker()).isNull();
    }

}