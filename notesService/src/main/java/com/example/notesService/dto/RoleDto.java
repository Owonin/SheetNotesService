package com.example.notesService.dto;

import com.example.notesService.entity.ERoles;
import lombok.*;

import java.util.List;

@Data
public class RoleDto {

    private Long id;

    private ERoles name;

    private List<UserDto> users;
}