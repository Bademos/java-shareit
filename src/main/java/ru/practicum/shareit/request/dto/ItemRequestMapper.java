package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.item.ItemDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

public class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto makeItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder().id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription())
                .requestorId(request.getRequestor().getId())
                .items(request.getItems() != null ? request.getItems().stream().map(ItemDtoMapper::makeItemDto).collect(Collectors.toList()) : null)
                .build();
    }

    public static ItemRequest makeItemRequestFromDto(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .created(requestDto.getCreated())
                .requestor(user)
                .description(requestDto.description)
                .build();
    }
}