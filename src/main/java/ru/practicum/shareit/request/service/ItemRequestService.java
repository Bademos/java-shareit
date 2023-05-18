package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto itemRequest, Integer userId);
    ItemRequestDto getRequestById(Integer userId, Integer integer);

    List<ItemRequestDto> getAllRequestByUser(Integer userId);
    List<ItemRequestDto> getAllRequestsByPages(Integer from, Integer size, Integer userId);
}
