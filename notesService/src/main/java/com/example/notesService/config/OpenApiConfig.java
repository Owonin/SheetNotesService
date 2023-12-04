package com.example.notesService.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "NotesService Api",
                contact = @Contact(
                        name = "Потрываев Александр",
                        email = "Alexpotr@yandex.ru"
                )
        )
)

public class OpenApiConfig {
}

