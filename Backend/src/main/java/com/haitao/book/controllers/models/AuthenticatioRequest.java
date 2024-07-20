package com.haitao.book.controllers.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class AuthenticatioRequest {

    @NotEmpty(message = "username name is required")
    @NotBlank(message = "username name is required")
    @Size(min = 5, message = "user should be longer")
    private String username;
    @NotEmpty(message = "password name is required")
    @NotBlank(message = "password name is required")
    @Size(min = 5, message = "Password should be 8 characters long minimum")
    private String password;

}
