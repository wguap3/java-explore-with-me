package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.dto.CompilationsUpdateDtoIn;
import ru.practicum.compilations.mapper.CompilationsMapper;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.compilations.repository.CompilationsCustomRepository;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final JdbcTemplate jdbcTemplate;
    private final CompilationsCustomRepository compilationsCustomRepository;

    @Override
    @Transactional
    public CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.save(compilationsMapper.mapCompilationsDtoInToCompilations(compilationsDtoIn));
        if (compilationsDtoIn.getEvents() != null && !compilationsDtoIn.getEvents().isEmpty()) {
            compilationsCustomRepository.saveCompilationEventsBatch(compilations.getId(), compilationsDtoIn.getEvents());
        }
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilations compilations = compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationsRepository.delete(compilations);
    }

    @Override
    @Transactional
    public CompilationsDtoOut updateCompilation(Long compId, CompilationsUpdateDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        if (compilationsDtoIn.getEvents() != null) {
            jdbcTemplate.update("DELETE FROM compilations_events AS ce WHERE ce.compilation_id = ?", compilations.getId());
            for (int i = 0; i < compilationsDtoIn.getEvents().size(); i++) {
                jdbcTemplate.update("INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)", compilations.getId(), compilationsDtoIn.getEvents().get(i));
            }
        }
        if (compilationsDtoIn.getPinned() != null) {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        if (compilationsDtoIn.getTitle() != null) {
            compilations.setTitle(compilationsDtoIn.getTitle());
        }
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilationsRepository.save(compilations));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned != null) {
            return compilationsRepository.getPublicCompByPinned(pinned, from, size).stream().map(compilationsMapper::mapCompilationsToCompilationsDtoOut).toList();
        } else {
            return compilationsRepository.getPublicComp(from, size).stream().map(compilationsMapper::mapCompilationsToCompilationsDtoOut).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationsDtoOut getPublicCompilationsById(Long compId) {
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }
}