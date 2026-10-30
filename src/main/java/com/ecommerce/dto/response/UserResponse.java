package com.ecommerce.dto.response;

import com.ecommerce.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}
