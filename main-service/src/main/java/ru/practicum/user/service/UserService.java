package ru.practicum.user.service;

import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;

import java.util.List;

public interface UserService {
    List<UserDtoOut> getUsers(Long[] ids, Integer from, Integer size);

    UserDtoOut addUser(UserDtoIn userDtoIn);

    void deleteUser(Long userId);

    UserShortDtoOut getUser(Long userId);
}
