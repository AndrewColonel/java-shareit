package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthor_IdAndItem_IdOrderByCreatedAsc(long userId, long itemId);

    List<Comment> findByItem_IdOrderByCreatedAsc(long itemId);

    List<Comment> findByItem_IdInOrderByCreatedAsc(List<Long> itemIdList);
}
