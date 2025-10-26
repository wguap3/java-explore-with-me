package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCreator(Long userId);

    @Query("select c from Comment as c where c.status = 'PUBLISHED' AND c.id IN ?1")
    List<Comment> getCommentsWithIds(List<Long> eventIds);

    @Query("select c from Comment as c where c.status = 'PENDING' AND c.id IN ?1")
    List<Comment> getPendingCommentsWithIds(List<Long> eventIds);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO comments_events (comment_id, event_id) VALUES (?1, ?2)", nativeQuery = true)
    void linkCommentToEvent(Long commentId, Long eventId);

    @Query(value = "SELECT ce.comment_id FROM comments_events ce WHERE ce.event_id = ?1", nativeQuery = true)
    List<Long> findCommentIdsByEventId(Long eventId);

    @Query(value = "SELECT ce.event_id FROM comments_events ce WHERE ce.comment_id = ?1", nativeQuery = true)
    List<Long> findEventIdsByCommentId(Long commentId);

}
