package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank
    public String name;
    @NotNull
    public String description;
    @NotNull
    public Boolean available;

    public BookingDtoForItem lastBooking;

    public BookingDtoForItem nextBooking;

    public List<CommentDto> comments;
}