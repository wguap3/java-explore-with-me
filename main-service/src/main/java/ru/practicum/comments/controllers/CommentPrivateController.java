package ru.practicum.comments.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDtoIn;
import ru.practicum.comments.dto.CommentSortDtoOut;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("comments/users/{userId}")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentSortDtoOut addComment(@PathVariable(name = "userId") Long userId,
                                        @RequestParam(name = "eventId") Long eventId,
                                        @Valid @RequestBody CommentDtoIn commentDtoIn, HttpServletRequest request) {
        log.info("POST/ Проверка параметров запроса метода addComment, userId - {}, eventId - {}, commentDtoIn - {}", userId, eventId, commentDtoIn);
        return commentService.addComment(userId, eventId, commentDtoIn, request);
    }

    @PatchMapping
    public CommentSortDtoOut updateComment(@PathVariable(name = "userId") Long userId,
                                           @RequestParam(name = "commentId") Long commentId,
                                           @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода updateComment, userId - {}, commentId - {}, commentDtoIn - {}", userId, commentId, commentDtoIn);
        return commentService.updateComment(userId, commentId, commentDtoIn);
    }

    @GetMapping
    public List<CommentSortDtoOut> getAllCommentsByUserId(@PathVariable(name = "userId") Long userId) {
        log.info("GET/ Проверка параметров запроса метода getAllCommentsByUserId, userId - {}", userId);
        return commentService.getAllCommentsByUserId(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "userId") Long userId,
                              @RequestParam(name = "commentId") Long commentId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteComment, userId - {}, commentId - {}", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }
}
