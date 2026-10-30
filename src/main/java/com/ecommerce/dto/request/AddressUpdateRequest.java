package com.ecommerce.dto.request;

import com.ecommerce.enums.AddressType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequest {

    private String fullName; //receiver name

    private String phone;

    private String city;

    private String district;

    private String state;

    @Size(min = 6,max = 6,message = "Pin code must be exactly 6 digits")
    private String pinCode;

    private AddressType addressType;

    private String addressLine;


}
