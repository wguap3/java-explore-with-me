package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.CommentDtoIn;
import ru.practicum.comments.dto.CommentDtoOut;
import ru.practicum.comments.dto.CommentSortDtoOut;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.dto.EventShortDtoOut;
import ru.practicum.user.dto.UserShortDtoOut;

@Mapper(componentModel = "spring")
public interface CommentMapper {


    @Mapping(target = "text", source = "text")
    Comment mapCommentDtoInToComment(CommentDtoIn commentDtoIn);


    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "creator", source = "creatorName")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "status", source = "comment.status")
    @Mapping(target = "eventAnnotation", source = "eventAnnotation")
    CommentSortDtoOut mapCommentToCommentShortDtoOut(
            Comment comment,
            String creatorName,
            String eventAnnotation
    );

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "userShortDtoOut", source = "userShortDtoOut")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "status", source = "comment.status")
    @Mapping(target = "eventShortDtoOut", source = "eventShortDtoOut")
    CommentDtoOut mapCommentToCommentDtoOut(
            Comment comment,
            UserShortDtoOut userShortDtoOut,
            EventShortDtoOut eventShortDtoOut
    );
}



