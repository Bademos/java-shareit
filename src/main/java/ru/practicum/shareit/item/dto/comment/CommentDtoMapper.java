package ru.practicum.shareit.item.dto.comment;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentDtoMapper {
    public static CommentDto makeCommentDto(Comment comment) {
        return CommentDto.builder().id(comment.getId())
                .itemId(comment.getItem().getId())
                .authorId(comment.getUser().getId())
                .text(comment.getText())
                .name(comment.getUser().getName())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment makeComment(CommentDto cmt, User user, Item item) {
        return Comment.builder().id(cmt.getId())
                .text(cmt.getText())
                .user(user)
                .item(item).build();
    }
}