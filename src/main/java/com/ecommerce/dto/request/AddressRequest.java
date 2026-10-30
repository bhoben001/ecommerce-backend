package com.ecommerce.dto.request;

import com.ecommerce.enums.AddressType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    @NotNull
    private String fullName;

    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @NotNull
    private String city;

    @NotNull
    private String district;

    @NotNull
    private String state;

    @Size(min = 6,max = 6,message = "Pin code must be exactly 6 digits")
    @NotNull
    private String pinCode;

    private AddressType addressType;

    @NotNull
    private String addressLine;
}
