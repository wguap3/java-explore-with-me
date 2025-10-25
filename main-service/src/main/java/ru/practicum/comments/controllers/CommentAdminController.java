package ru.practicum.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDtoOut;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("{userId}")
    public List<CommentDtoOut> getAdminCommentsByUserId(@PathVariable(name = "userId") Long userId) {
        log.info("GET/ Проверка параметров запроса метода getAdminCommentsByUserId, userId - {},", userId);
        return commentService.getAdminCommentsByUserId(userId);
    }

    @GetMapping()
    public List<CommentDtoOut> getAdminCommentsByEventId(@RequestParam(name = "eventId") Long eventId) {
        log.info("GET/ Проверка параметров запроса метода getAdminCommentsByEventId, eventId - {},", eventId);
        return commentService.getAdminCommentsByEventId(eventId);
    }

    @GetMapping("/pending")
    public List<CommentDtoOut> getAdminPendingCommentsByEventId(@RequestParam(name = "eventId") Long eventId) {
        log.info("GET/ Проверка параметров запроса метода getAdminPendingCommentsByEventId, eventId - {},", eventId);
        return commentService.getAdminPendingCommentsByEventId(eventId);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminComment(@PathVariable(name = "commentId") Long commentId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteAdminComment, commentId - {},", commentId);
        commentService.deleteAdminComment(commentId);
    }

    @PatchMapping("{commentId}")
    public CommentDtoOut conformationAdminComment(@PathVariable(name = "commentId") Long commentId,
                                                  @RequestParam(name = "accept") Boolean accept) {
        log.info("PATCH/ Проверка параметров запроса метода conformationAdminComment, commentId - {}, accept - {}", commentId, accept);
        return commentService.conformationAdminComment(commentId, accept);
    }
}
