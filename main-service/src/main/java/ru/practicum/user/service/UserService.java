package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto findById(Long userId);

    List<UserDto> getAllUsers(int from, int size);

    void deleteUser(Long userId);

    User findByIdOrThrow(Long userId);

    List<UserDto> getUsersByIds(List<Long> ids);
}
