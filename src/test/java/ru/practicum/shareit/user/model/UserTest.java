package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class UserTest {
    @Autowired
    private JacksonTester<User> json;

    @Test
    void userTest() throws Exception {
        User user = User.builder()
                .id(1)
                .name("test")
                .email("email@test.com").build();

        JsonContent<User> res = json.write(user);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(res).extractingJsonPathStringValue("$.email").isEqualTo("email@test.com");
    }
}
