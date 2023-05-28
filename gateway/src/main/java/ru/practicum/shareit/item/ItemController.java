package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
	private final ItemClient itemClient;

	@GetMapping ("/{id}")
	public ResponseEntity<Object> getById(@PathVariable int id, @RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Got request for user with id:{}", id);
		return itemClient.getById(userId, id);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") long useId ) {
		return itemClient.create(useId, item);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody ItemDto item, @PathVariable int id,@RequestHeader("X-Sharer-User-Id") long userId ) {
		return itemClient.update(id, userId, item);
	}


	@PostMapping("/{id}/comment")
	public ResponseEntity<Object>  createComment(@PathVariable int id,
									@Valid @RequestBody CommentDto commentDto,
									@RequestHeader(name = "X-Sharer-User-Id") int userId) {
		log.info("Got request from user with id {} to create comment for item with id {}", userId, id);
		return itemClient.createComment(userId, id, commentDto);
	}

	@GetMapping("/search")
	public ResponseEntity<Object>  searchByRequest(@RequestHeader(name = "X-Sharer-User-Id") int userId,
										 			@RequestParam String text,
												   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
												   @RequestParam(defaultValue = "10") @Positive int size) {

		log.info("Got request from user with id: {} to search items contains the text: {}", userId, text);
		if (text.isBlank()) {
			return ResponseEntity.ok(Collections.emptyList());
		}
		return itemClient.searchItems(userId,text,from,size);
	}


}
