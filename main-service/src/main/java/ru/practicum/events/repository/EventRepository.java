package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("""
            SELECT e FROM Event e
            WHERE (:userIds IS NULL OR e.initiator.id IN :userIds)
              AND (:states IS NULL OR e.state IN :states)
              AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)
              AND (COALESCE(:start, null) IS NULL OR e.eventDate >= :start)
              AND (COALESCE(:end, null) IS NULL OR e.eventDate <= :end)
            """)
    List<Event> findAdminEvents(
            List<Long> userIds,
            List<EventStatus> states,
            List<Long> categoryIds,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @Query("""
            SELECT e FROM Event e
            WHERE e.state = 'PUBLISHED'
              AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                   OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
              AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)
              AND (:paid IS NULL OR e.paid = :paid)
              AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)
            """)
    List<Event> findPublicEvents(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable
    );

    Page<Event> findAllByState(EventStatus state, Pageable pageable);


    Page<Event> findAllByStateAndCategoryId(EventStatus state, Long categoryId, Pageable pageable);


    Optional<Event> findByIdAndState(Long id, EventStatus state);
    @Query("SELECT e FROM Event e " +
            "WHERE e.state IN :states " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate >= :start " +
            "AND (:end IS NULL OR e.eventDate <= :end) " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
            "AND (:onlyAvailable = false OR e.participantLimit IS NULL OR e.participantLimit > SIZE(e.requests WHERE e.state = 'CONFIRMED'))")
    List<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("states") List<EventStatus> states,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("onlyAvailable") boolean onlyAvailable,
                                 Pageable pageable);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    Long countConfirmedRequests(@Param("eventId") Long eventId);

}

