package com.example.authService.mapper;

import com.example.authService.dto.UserDto;
import com.example.authService.entity.UserEntity;

public class UserMapper {
    public static UserEntity userDtoToUserEntity(UserDto dto){
        UserEntity entity = new UserEntity();
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setPassword(dto.getPassword());

        return entity;
    }
}
