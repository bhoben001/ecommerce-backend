package com.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email(message = "please enter a valid email")
    String email;

    @Size(min=8, message = "password must be at-least 8 character")
    String password;
}
