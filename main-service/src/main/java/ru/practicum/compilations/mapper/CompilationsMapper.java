package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.event.dto.EventShortDtoOut;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CompilationsMapper {

    @Mapping(target = "pinned", expression = "java(compilationsDtoIn.getPinned() == null ? false : compilationsDtoIn.getPinned())")
    @Mapping(target = "title", source = "compilationsDtoIn.title")
    public abstract Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn);

    @Mapping(target = "id", source = "compilations.id")
    @Mapping(target = "pinned", source = "compilations.pinned")
    @Mapping(target = "title", source = "compilations.title")
    @Mapping(target = "events", source = "events")
    public abstract CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations, List<EventShortDtoOut> events);
}





