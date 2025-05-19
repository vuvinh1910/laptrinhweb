package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.UserCreationRequest;
import com.example.web_nhom_5.dto.request.UserProfileUpdateRequest;
import com.example.web_nhom_5.dto.request.UserUpdateRequest;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.UserEntity;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserEntity toUser(UserCreationRequest request);
    UserResponse toUserResponse(UserEntity users);

    @Mapping(target = "userName", conditionQualifiedByName = "isNotEmpty")
    @Mapping(target = "email", conditionQualifiedByName = "isNotEmpty")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "fullName", conditionQualifiedByName = "isNotEmpty")
    void updateUser(@MappingTarget UserEntity users, UserUpdateRequest request);


    @Mapping(target = "roles" , ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "userName", ignore = true)
    void updateProfile(@MappingTarget UserEntity users, UserProfileUpdateRequest request);

    @Named("isNotEmpty")
    default boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
