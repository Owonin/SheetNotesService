package com.example.fileService.entity;

import lombok.Data;

import java.util.List;


@Data
public class UserEntity {
    private String login;
    private List<String> roles;

}