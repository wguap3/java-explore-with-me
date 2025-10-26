package ru.practicum.comments.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.comments.dto.CommentDtoIn;
import ru.practicum.comments.dto.CommentDtoOut;
import ru.practicum.comments.dto.CommentSortDtoOut;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.enums.ComState;
import ru.practicum.event.dto.EventDtoOut;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.stats.StatsService;
import ru.practicum.user.dto.UserShortDtoOut;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper commentMapper;
    private final CategoryService categoryService;
    private final ParticipationRepository participationRepository;
    private final StatsService statsService;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public CommentSortDtoOut addComment(Long userId, Long eventId, CommentDtoIn commentDtoIn, HttpServletRequest request) {
        UserShortDtoOut user = userService.getUser(userId);
        EventDtoOut eventDto = eventService.getPublicEventById(eventId, request);
        Comment comment = commentMapper.mapCommentDtoInToComment(commentDtoIn);
        comment.setCreator(userId);
        comment.setStatus(ComState.PENDING);
        Comment savedComment = commentRepository.save(comment);
        commentRepository.linkCommentToEvent(savedComment.getId(), eventId);
        String creatorName = user.getName();
        String eventAnnotation = eventDto.getAnnotation();
        return commentMapper.mapCommentToCommentShortDtoOut(savedComment, creatorName, eventAnnotation);
    }


    @Transactional
    @Override
    public CommentSortDtoOut updateComment(Long userId, Long commentId, CommentDtoIn commentDtoIn) {
        UserShortDtoOut user = userService.getUser(userId);
        Comment comment = findCommentWithCheck(commentId);
        checkCommentAndUserId(userId, comment);
        comment.setText(commentDtoIn.getText());
        comment.setStatus(ComState.PENDING);
        Comment updatedComment = commentRepository.save(comment);
        List<Long> eventIds = commentRepository.findEventIdsByCommentId(commentId);
        String eventAnnotation = eventIds.isEmpty() ? null : eventService.getPublishEventById(eventIds.get(0)).getAnnotation();
        return commentMapper.mapCommentToCommentShortDtoOut(updatedComment, user.getName(), eventAnnotation);
    }


    @Override
    public List<CommentSortDtoOut> getAllCommentsByUserId(Long userId) {
        UserShortDtoOut user = userService.getUser(userId);
        String creatorName = user.getName();
        List<Comment> comments = commentRepository.findAllByCreator(userId);
        return comments.stream().map(comment -> {
            List<Long> eventIds = commentRepository.findEventIdsByCommentId(comment.getId());
            String eventAnnotation = null;
            if (!eventIds.isEmpty()) {
                Event event = eventService.getPublishEventById(eventIds.get(0));
                eventAnnotation = event.getAnnotation();
            }
            return commentMapper.mapCommentToCommentShortDtoOut(comment, creatorName, eventAnnotation);

        }).toList();
    }


    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        userService.getUser(userId);
        Comment comment = findCommentWithCheck(commentId);
        checkCommentAndUserId(userId, comment);
        commentRepository.delete(comment);
    }


    @Override
    public List<CommentDtoOut> getAdminCommentsByUserId(Long userId) {
        UserShortDtoOut userShortDtoOut = userService.getUser(userId);
        List<Comment> comments = commentRepository.findAllByCreator(userId);
        return comments.stream().map(comment -> {

            List<Long> eventIds = commentRepository.findEventIdsByCommentId(comment.getId());
            EventShortDtoOut eventShortDtoOut = null;
            if (!eventIds.isEmpty()) {
                Long eventId = eventIds.get(0);
                Event event = eventService.getPublishEventById(eventId);
                CategoryDtoOut category = categoryService.getCategory(event.getCategory());
                UserShortDtoOut initiator = userService.getUser(event.getInitiator());
                Long confirmedRequests = participationRepository.countByEventIdAndConfirmed(event.getId());
                Long views = 0L;
                try {
                    List<String> uris = List.of("/events/" + event.getId());
                    List<ViewStatsDto> stats = statsService.getStats(event.getCreatedOn(), LocalDateTime.now(), uris, true);
                    if (!stats.isEmpty()) {
                        views = stats.get(0).getHits();
                    }
                } catch (Exception e) {
                    log.error("Ошибка получения статистики просмотров для события id={}", event.getId(), e);
                }

                eventShortDtoOut = eventMapper.mapEventToEventShortDtoOut(event, category, initiator, confirmedRequests, views);
            }

            return commentMapper.mapCommentToCommentDtoOut(comment, userShortDtoOut, eventShortDtoOut);

        }).toList();
    }


    @Override
    public List<CommentDtoOut> getAdminCommentsByEventId(Long eventId) {
        Event event = eventService.getPublishEventById(eventId);
        List<Long> commentIds = commentRepository.findCommentIdsByEventId(eventId);

        List<Comment> comments = commentRepository.getCommentsWithIds(commentIds);

        CategoryDtoOut category = categoryService.getCategory(event.getCategory());
        UserShortDtoOut initiator = userService.getUser(event.getInitiator());
        Long confirmedRequests = participationRepository.countByEventIdAndConfirmed(event.getId());
        Long views = 0L;
        try {
            List<String> uris = List.of("/events/" + event.getId());
            List<ViewStatsDto> stats = statsService.getStats(event.getCreatedOn(), LocalDateTime.now(), uris, true);
            if (!stats.isEmpty()) {
                views = stats.get(0).getHits();
            }
        } catch (Exception e) {
            log.error("Ошибка получения статистики просмотров для события id={}", event.getId(), e);
        }
        EventShortDtoOut eventShortDtoOut = eventMapper.mapEventToEventShortDtoOut(event, category, initiator, confirmedRequests, views);

        return comments.stream().map(comment -> {

            UserShortDtoOut userShortDtoOut = userService.getUser(comment.getCreator());

            return commentMapper.mapCommentToCommentDtoOut(comment, userShortDtoOut, eventShortDtoOut);

        }).toList();
    }


    @Transactional
    @Override
    public void deleteAdminComment(Long commentId) {
        Comment comment = findCommentWithCheck(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public CommentDtoOut conformationAdminComment(Long commentId, Boolean accept) {
        Comment comment = findCommentWithCheck(commentId);
        if (comment.getStatus().equals(ComState.PENDING) && !accept) {
            comment.setStatus(ComState.REJECTED);
        } else if (comment.getStatus().equals(ComState.PENDING) && accept) {
            comment.setStatus(ComState.PUBLISHED);
        } else {
            throw new ConflictException("Подтверждать или отклонять комментарии можно только со статусом ожидания!");
        }
        Comment updatedComment = commentRepository.save(comment);
        UserShortDtoOut userShortDtoOut = userService.getUser(updatedComment.getCreator());

        List<Long> eventIds = commentRepository.findEventIdsByCommentId(commentId);
        EventShortDtoOut eventShortDtoOut = null;
        if (!eventIds.isEmpty()) {
            Long eventId = eventIds.get(0);
            Event event = eventService.getPublishEventById(eventId);
            CategoryDtoOut category = categoryService.getCategory(event.getCategory());
            UserShortDtoOut initiator = userService.getUser(event.getInitiator());
            Long confirmedRequests = participationRepository.countByEventIdAndConfirmed(event.getId());
            Long views = 0L;
            try {
                List<String> uris = List.of("/events/" + event.getId());
                List<ViewStatsDto> stats = statsService.getStats(event.getCreatedOn(), LocalDateTime.now(), uris, true);
                if (!stats.isEmpty()) {
                    views = stats.get(0).getHits();
                }
            } catch (Exception e) {
                log.error("Ошибка получения статистики просмотров для события id={}", event.getId(), e);
            }

            eventShortDtoOut = eventMapper.mapEventToEventShortDtoOut(event, category, initiator, confirmedRequests, views);
        }

        return commentMapper.mapCommentToCommentDtoOut(updatedComment, userShortDtoOut, eventShortDtoOut);
    }


    @Override
    public List<CommentDtoOut> getAdminPendingCommentsByEventId(Long eventId) {
        Event event = eventService.getPublishEventById(eventId);

        List<Long> commentIds = commentRepository.findCommentIdsByEventId(eventId);

        List<Comment> comments = commentRepository.getPendingCommentsWithIds(commentIds);

        CategoryDtoOut category = categoryService.getCategory(event.getCategory());
        UserShortDtoOut initiator = userService.getUser(event.getInitiator());
        Long confirmedRequests = participationRepository.countByEventIdAndConfirmed(event.getId());
        Long views = 0L;
        try {
            List<String> uris = List.of("/events/" + event.getId());
            List<ViewStatsDto> stats = statsService.getStats(event.getCreatedOn(), LocalDateTime.now(), uris, true);
            if (!stats.isEmpty()) {
                views = stats.get(0).getHits();
            }
        } catch (Exception e) {
            log.error("Ошибка получения статистики просмотров для события id={}", event.getId(), e);
        }
        EventShortDtoOut eventShortDtoOut = eventMapper.mapEventToEventShortDtoOut(event, category, initiator, confirmedRequests, views);

        return comments.stream().map(comment -> {

            UserShortDtoOut userShortDtoOut = userService.getUser(comment.getCreator());

            return commentMapper.mapCommentToCommentDtoOut(comment, userShortDtoOut, eventShortDtoOut);

        }).toList();
    }


    public Comment findCommentWithCheck(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    public void checkCommentAndUserId(Long userId, Comment comment) {
        if (!userId.equals(comment.getCreator())) {
            throw new ConflictException("У вас нет прав на изменение этого комментария!");
        }
    }

}
