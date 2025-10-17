package ru.practicum.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByRequesterAndEvent(Long userId, Long eventId);

    List<Participation> findAllByEvent(Long eventId);

    List<Participation> findAllByRequester(Long userId);

    Long countByEvent(Long eventId);

    @Query("select count(p) from Participation as p where (p.status = 'PUBLISHED' or p.status = 'CANCELED') and p.id IN ?1")
    Long countBadReq(List<Long> requestIds);

    @Query("select p from Participation as p where p.id IN ?1")
    List<Participation> participationReq(List<Long> requestIds);

    @Query("select count(p) from Participation as p where p.event = ?1 and p.status = 'CONFIRMED'")
    Long countByEventIdAndConfirmed(Long eventId);
}
