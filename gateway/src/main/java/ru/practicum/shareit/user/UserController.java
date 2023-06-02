package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
	private final UserClient userClient;

	@GetMapping
	public ResponseEntity<Object> getAll() {
		log.info("Got request for list of all users");
		return userClient.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
		log.info("Got request for user with id: {}", id);
		return userClient.getById(id);
	}

	@PostMapping
	public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
		log.info("Got request to create new user");
		return userClient.create(userDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
		log.info("Got request for user with id:{}", id);
		return userClient.update(id, userDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		log.info("Got request to delete user with id:{}", id);
		return userClient.delete(id);
	}
}
