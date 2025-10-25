package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.dto.CompilationsUpdateDtoIn;

import java.util.List;

public interface CompilationsService {
    CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn);

    void deleteCompilation(Long compId);

    CompilationsDtoOut updateCompilation(Long compId, CompilationsUpdateDtoIn compilationsDtoIn);

    List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size);

    CompilationsDtoOut getPublicCompilationsById(Long compId);
}
