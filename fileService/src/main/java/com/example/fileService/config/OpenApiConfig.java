package com.example.fileService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "File service API",
                contact = @Contact(
                        name = "Potryvaev Alexandr",
                        email = "Alexpotr@yandex.ru"
                )
        )
)

public class OpenApiConfig {
}
