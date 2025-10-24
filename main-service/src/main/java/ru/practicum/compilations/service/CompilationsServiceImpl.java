package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.dto.CompilationsUpdateDtoIn;
import ru.practicum.compilations.mapper.CompilationsMapper;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.compilations.repository.CompilationsCustomRepository;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final CompilationsCustomRepository compilationsCustomRepository;

    @Override
    @Transactional
    public CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.save(compilationsMapper.mapCompilationsDtoInToCompilations(compilationsDtoIn));
        if (compilationsDtoIn.getEvents() != null && !compilationsDtoIn.getEvents().isEmpty()) {
            compilationsCustomRepository.saveCompilationEventsBatch(compilations.getId(), compilationsDtoIn.getEvents());
        }
        List<EventShortDtoOut> events = new ArrayList<>();
        if (compilationsDtoIn.getEvents() != null && !compilationsDtoIn.getEvents().isEmpty()) {
            List<Event> eventEntities = eventRepository.findAllById(compilationsDtoIn.getEvents());
            events = eventEntities.stream()
                    .map(event -> eventService.buildEventShortDtoOut(event, 0L))
                    .toList();
        }
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations, events);
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
            compilationsCustomRepository.updateCompilationEvents(compilations.getId(), compilationsDtoIn.getEvents());
        }
        if (compilationsDtoIn.getPinned() != null) {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        if (compilationsDtoIn.getTitle() != null) {
            compilations.setTitle(compilationsDtoIn.getTitle());
        }
        compilations = compilationsRepository.save(compilations);

        List<Long> eventIds = compilationsCustomRepository.getEventIdsByCompilationId(compId);
        List<Event> eventEntities = eventRepository.findAllById(eventIds);

        List<EventShortDtoOut> events = eventEntities.stream()
                .map(event -> eventService.buildEventShortDtoOut(event, null)) // views подтянутся внутри метода
                .toList();

        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations, events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilations> compilations;
        if (pinned != null) {
            compilations = compilationsRepository.getPublicCompByPinned(pinned, from, size);
        } else {
            compilations = compilationsRepository.getPublicComp(from, size);
        }

        return compilations.stream()
                .map(comp -> {
                    List<Long> eventIds = compilationsCustomRepository.getEventIdsByCompilationId(comp.getId());
                    List<Event> eventEntities = eventRepository.findAllById(eventIds);
                    List<EventShortDtoOut> events = eventEntities.stream()
                            .map(event -> eventService.buildEventShortDtoOut(event, null)) // views подтянутся внутри метода
                            .toList();
                    return compilationsMapper.mapCompilationsToCompilationsDtoOut(comp, events);
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public CompilationsDtoOut getPublicCompilationsById(Long compId) {
        Compilations comp = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        List<Long> eventIds = compilationsCustomRepository.getEventIdsByCompilationId(compId);

        List<Event> eventEntities = eventRepository.findAllById(eventIds);

        List<EventShortDtoOut> events = eventEntities.stream()
                .map(event -> eventService.buildEventShortDtoOut(event, null)) // просмотры подтянутся внутри метода
                .toList();

        return compilationsMapper.mapCompilationsToCompilationsDtoOut(comp, events);
    }


}