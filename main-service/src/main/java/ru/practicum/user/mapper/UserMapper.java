package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User mapUserDtoInToUser(UserDtoIn userDtoIn);

    UserDtoOut mapUserToUserDtoOut(User user);

    UserShortDtoOut mapUserToUserShortDtoOut(User user);
}

