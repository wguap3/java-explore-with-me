package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned);

    CompilationDto getCompilation(Long compId);

    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

    Compilation findByIdOrThrow(Long comId);

}
