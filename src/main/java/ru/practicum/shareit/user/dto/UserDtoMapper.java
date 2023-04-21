package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDtoMapper {
    public static UserDto makeUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User makeUserFromDto(UserDto userDto, int userId) {
        return User.builder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User update(User oldUser, User newUser) {
        var tempUser = oldUser.toBuilder();
        if (newUser.getName() != null && !newUser.getName().isEmpty()) {
            tempUser.name(newUser.getName());
        }
        if (newUser.getEmail() != null
                && !newUser.getEmail().isEmpty()) {
            tempUser.email(newUser.getEmail());
        }
        return tempUser.build();
    }
}