package com.example.authService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Auth service API",
                contact = @Contact(
                        name = "Potryvaev Alexandr",
                        email = "Alexpotr@yandex.ru"
                )
        )
)
public class OpenApiConfig {

}