package com.example.notesService.dto;

import lombok.Data;

import java.util.List;


@Data
public class UserDto {
    private String login;
    private List<String> roles;

}