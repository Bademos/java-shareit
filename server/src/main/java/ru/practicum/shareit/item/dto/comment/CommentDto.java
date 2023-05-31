package ru.practicum.shareit.item.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private int id;
    private String text;
    private int authorId;
    private int itemId;
    private String authorName;
    private String name;
    private LocalDateTime created;
}