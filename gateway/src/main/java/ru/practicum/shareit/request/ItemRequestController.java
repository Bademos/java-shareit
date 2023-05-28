package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object>  createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") int userId,
											@Valid @RequestBody ItemRequestDto itemRequest) {
		log.info("Got Item Request");
		return itemRequestClient.create(userId, itemRequest);

	}

	@GetMapping("/{id}")
	public ResponseEntity<Object>  getRequestById(@RequestHeader(name = "X-Sharer-User-Id") int userId,
										 @PathVariable int id) {
		log.info("Got request for item request");
		return itemRequestClient.getById(userId, id);
	}

	@GetMapping()
	public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
		log.info("Got request for all item request of User with id:{}", userId);
		return itemRequestClient.getAllByUser(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object>  getAllRequestsByUserByPages(@RequestHeader(name = "X-Sharer-User-Id") int userId,
															@RequestParam(defaultValue = "0") @PositiveOrZero int from,
															@RequestParam(defaultValue = "10") @Positive int size) {
		log.info("Got request for all item request of User with id:{} in pages", userId);
		return itemRequestClient.getByPage(from, size, userId);
	}


}
