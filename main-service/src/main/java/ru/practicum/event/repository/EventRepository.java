package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM events AS e WHERE e.initiator = :userId ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getEvents(@Param("userId") Long userId, @Param("from") Integer from, @Param("size") Integer size);

    Optional<Event> findByIdAndInitiator(Long eventId, Long userId);

    @Query("select e from Event as e where e.id = ?1 AND e.state = 'PUBLISHED'")
    Optional<Event> getPublicEventById(Long eventId);

    @Query(value = "SELECT * FROM events AS e " +
            "WHERE (e.annotation ILIKE CONCAT('%', :lowText, '%') " +
            "OR e.description ILIKE CONCAT('%', :lowText, '%')) " +
            "AND e.event_date > :rangeStart " +
            "AND e.event_date < :rangeEnd " +
            "AND e.state = 'PUBLISHED'",
            nativeQuery = true)
    List<Event> getPublicEventByTextAndStartAndEnd(@Param("lowText") String lowText,
                                                   @Param("rangeStart") LocalDateTime rangeStart,
                                                   @Param("rangeEnd") LocalDateTime rangeEnd);


    @Query(value = "SELECT * FROM events AS e " +
            "WHERE (e.annotation ILIKE CONCAT('%', :lowText, '%') " +
            "OR e.description ILIKE CONCAT('%', :lowText, '%')) " +
            "AND e.event_date > :rangeStart " +
            "AND e.state = 'PUBLISHED'",
            nativeQuery = true)
    List<Event> getPublicEventByTextAndStart(@Param("lowText") String lowText,
                                             @Param("rangeStart") LocalDateTime rangeStart);

    @Query(value = "SELECT * FROM events AS e " +
            "WHERE (e.annotation ILIKE CONCAT('%', :lowText, '%') " +
            "OR e.description ILIKE CONCAT('%', :lowText, '%')) " +
            "AND e.event_date < :rangeEnd AND e.state = 'PUBLISHED'",
            nativeQuery = true)
    List<Event> getPublicEventByTextAndEnd(@Param("lowText") String lowText,
                                           @Param("rangeEnd") LocalDateTime rangeEnd);


    @Query("SELECT e FROM Event e " +
            "WHERE (e.annotation ILIKE CONCAT('%', ?1, '%') " +
            "OR e.description ILIKE CONCAT('%', ?1, '%')) " +
            "AND e.state = 'PUBLISHED'")
    List<Event> getPublicEventByText(String lowText);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.event_date < :rangeEnd ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByStartAndEnd(@Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e " +
            "WHERE e.event_date < :rangeEnd " +
            "ORDER BY e.id " +
            "OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByStart(@Param("rangeEnd") LocalDateTime rangeEnd,
                                     @Param("from") Integer from,
                                     @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date < :rangeEnd OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByEnd(@Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEvent(@Param("from") Integer from, @Param("size") Integer size);


    @Query(value = "SELECT * FROM events AS e WHERE e.initiator IN :ids AND e.category IN :category", nativeQuery = true)
    List<Event> getAdminEventsByIdsAndCategory(@Param("ids") List<Long> ids, @Param("category") Long[] category);

    @Query(value = "SELECT * FROM events AS e WHERE e.initiator IN :ids", nativeQuery = true)
    List<Event> getAdminEventsByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM events AS e WHERE e.category IN :category ", nativeQuery = true)
    List<Event> getAdminEventsByCategory(@Param("category") Long[] category);

    @Query(value = "SELECT * FROM events AS e WHERE e.state IN :states", nativeQuery = true)
    List<Event> getAdminEventsInStates(@Param("states") List<String> states);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids AND e.state IN :states", nativeQuery = true)
    List<Event> getAdminEventsInIdsAndStates(@Param("ids") List<Long> ids, @Param("states") List<String> states);


    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.event_date < :rangeEnd AND e.id IN :ids ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByStartAndEnd(@Param("ids") List<Long> ids, @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.id IN :ids ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByStart(@Param("ids") List<Long> ids, @Param("rangeStart") LocalDateTime rangeStart, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date < :rangeEnd AND e.id IN :ids ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByEnd(@Param("ids") List<Long> ids, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids ORDER BY e.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIds(@Param("ids") List<Long> ids, @Param("from") Integer from, @Param("size") Integer size);

    @Query("select e from Event as e where e.id IN ?1")
    List<Event> getCompilationsEvents(List<Long> ids);

    List<Event> findAllByCategory(Long catId);
}
