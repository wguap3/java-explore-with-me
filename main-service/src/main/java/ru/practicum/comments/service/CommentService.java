package ru.practicum.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.comments.dto.CommentDtoIn;
import ru.practicum.comments.dto.CommentDtoOut;
import ru.practicum.comments.dto.CommentSortDtoOut;

import java.util.List;

public interface CommentService {
    CommentSortDtoOut addComment(Long userId, Long eventId, CommentDtoIn commentDtoIn, HttpServletRequest request);

    CommentSortDtoOut updateComment(Long userId, Long commentId, CommentDtoIn commentDtoIn);

    List<CommentSortDtoOut> getAllCommentsByUserId(Long userId);

    List<CommentDtoOut> getAdminCommentsByUserId(Long userId);

    void deleteComment(Long userId, Long commentId);

    List<CommentDtoOut> getAdminCommentsByEventId(Long eventId);

    void deleteAdminComment(Long commentId);

    CommentDtoOut conformationAdminComment(Long commentId, Boolean accept);

    List<CommentDtoOut> getAdminPendingCommentsByEventId(Long eventId);

}
