package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDtoOut> getUsers(@RequestParam(name = "ids", required = false) Long[] ids,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getUsers, ids - {}, from - {}, size - {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoOut addUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addUser, userDtoIn - {}", userDtoIn);
        return userService.addUser(userDtoIn);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteUser, userId - {}", userId);
        return userService.deleteUser(userId);
    }
}