package ru.practicum.compilations.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationsDtoIn;
import ru.practicum.compilations.dto.CompilationsDtoOut;
import ru.practicum.compilations.dto.CompilationsUpdateDtoIn;
import ru.practicum.compilations.service.CompilationsService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationsAdminController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationsDtoOut addCompilation(@Valid @RequestBody CompilationsDtoIn compilationsDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addCompilation, compilationsDtoIn - {}", compilationsDtoIn);
        return compilationsService.addCompilation(compilationsDtoIn);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteCompilation, compId - {}", compId);
        compilationsService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationsDtoOut updateCompilation(@PathVariable(name = "compId") Long compId,
                                                @Valid @RequestBody CompilationsUpdateDtoIn compilationsDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateCompilation, compId - {}, compilationsDtoIn - {}", compId, compilationsDtoIn);
        return compilationsService.updateCompilation(compId, compilationsDtoIn);
    }

}
