package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email уже используется: " + user.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }


    @Override
    public UserDto findById(Long userId) {
        User user = findByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    public List<UserDto> getAllUsers(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return userRepository.findAll(page).stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }
}
