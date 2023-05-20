package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoTest {
    User user = User.builder()
            .id(1)
            .name("userA")
            .email("userA@yandex.ru")
            .build();
    UserDto userDto = UserDto.builder()
            .id(1)
            .name("userA")
            .email("userA@yandex.ru").build();

    @Test
    void makeUserFromDtoTest() {
        User userResponse = UserDtoMapper.makeUserFromDto(userDto, user.getId());
        assertEquals(userResponse.getName(), user.getName());
        assertEquals(userResponse.getEmail(), user.getEmail());
    }

    @Test
    void makeUserDtoFromUSerTest() {
        UserDto userResponseDto = UserDtoMapper.makeUserDto(user);
        assertEquals(userResponseDto.getName(), user.getName());
        assertEquals(userResponseDto.getEmail(), user.getEmail());
    }


}
