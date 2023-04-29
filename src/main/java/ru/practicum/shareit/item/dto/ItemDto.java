package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private int id;
    @NotBlank
    public String name;
    @NotNull
    public String description;
    @NotNull
    public Boolean available;
}
