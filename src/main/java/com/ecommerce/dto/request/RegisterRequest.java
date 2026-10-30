package com.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "name is required")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "please enter valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 50, message = "password must be 8 to 50 characters")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "phone must be exactly 10 digits")
    private String phone;   // optional
}
