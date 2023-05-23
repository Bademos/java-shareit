package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
public class ItemTest {
    @Autowired
    private JacksonTester<Item> json;

    @Test
    void Test() throws Exception {
        Item item = Item.builder()
                .id(1)
                .name("test")
                .owner(User.builder().id(1).name("test").email("test@test").build())
                .available(true)
                .description("no description").build();

        JsonContent<Item> res = json.write(item);


        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
    }
}
