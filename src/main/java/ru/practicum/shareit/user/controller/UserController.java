package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Got request for list of all users");
        return service.getAll()
                .stream()
                .map(UserDtoMapper::makeUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {
        log.info("Got request for user with id: {}", id);
        return UserDtoMapper.makeUserDto(service.getById(id));
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = UserDtoMapper.makeUserFromDto(userDto, userDto.getId());
        log.info("Got request to create new user");
        return UserDtoMapper.makeUserDto(service.create(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable int id) {
        log.info("Got request for user with id:{}", id);
        User user = UserDtoMapper.makeUserFromDto(userDto, id);
        return UserDtoMapper.makeUserDto(service.update(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Got request to delete user with id:{}", id);
        service.removeUser(id);
    }
}