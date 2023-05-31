package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestTest {
    @Autowired
    private JacksonTester<ItemRequest> jacksonTester;

    @Test
    void serializeDateTest() throws IOException {
        ItemRequest request = ItemRequest.builder()
                .id(1)
                .description("boring")
                .items(Collections.emptyList())
                .created(LocalDateTime.of(1992,4,22,12,12,12))
                .requestor(null).build();

        JsonContent<ItemRequest> jsonRequest = jacksonTester.write(request);
        assertThat(jsonRequest).extractingJsonPathStringValue("$.created").isEqualTo("1992-04-22T12:12:12");
        assertThat(jsonRequest).extractingJsonPathStringValue("$.description").isEqualTo("boring");
    }
}
