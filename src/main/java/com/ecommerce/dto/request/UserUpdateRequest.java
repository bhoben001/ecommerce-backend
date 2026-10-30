package com.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;

    @Pattern(regexp = "^[0-9]{10}$", message = "phone must be exactly 10 digits")
    private String phone;
}