package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemIdOrderByIdAsc(int itemId);

    @Query("select comment from Comment comment "
            + " where comment.item.id in ?1")
    List<Comment> findAllComments(List<Integer> id);

}