package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilations.model.Compilations;

import java.util.List;

public interface CompilationsRepository extends JpaRepository<Compilations, Long> {

    @Query(value = "SELECT * FROM compilations AS c WHERE c.pinned = ?1  ORDER BY c.id OFFSET ?2 LIMIT ?3", nativeQuery = true)
    List<Compilations> getPublicCompByPinned(Boolean pinned, Integer from, Integer size);

    @Query(value = "SELECT * FROM compilations AS c ORDER BY c.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<Compilations> getPublicComp(@Param("from") Integer from, @Param("size") Integer size);


}
