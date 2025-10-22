package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Override
    public List<UserDtoOut> getUsers(Long[] ids, Integer from, Integer size) {
        if (ids == null) {
            return userRepository.findUsersWithLimit(from, size).stream().map(mapper::mapUserToUserDtoOut).toList();
        } else {
            return userRepository.findUsersByIdsWithLimit(ids, from, size).stream().map(mapper::mapUserToUserDtoOut).toList();
        }
    }

    @Transactional
    @Override
    public UserDtoOut addUser(UserDtoIn userDtoIn) {
        if (userRepository.findByEmail(userDtoIn.getEmail()) != null) {
            throw new ConflictException("Email пользователя должно быть уникальным!");
        }
        return mapper.mapUserToUserDtoOut(userRepository.save(mapper.mapUserDtoInToUser(userDtoIn)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(user);

    }

    @Override
    public UserShortDtoOut getUser(Long userId) {
        return mapper.mapUserToUserShortDtoOut(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found")));
    }
}