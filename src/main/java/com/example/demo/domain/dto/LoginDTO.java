package com.example.demo.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "username không được trống")
    private String username;

    @NotBlank(message = "password không được trống")
    private String password;
}
