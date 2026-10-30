package com.ecommerce.dto.response;

import com.ecommerce.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {

    private Long id;

    private Long userId;

    private String fullName;
    private String phone;

    private String city;

    private String district;

    private String state;

    private String pinCode;

    private AddressType addressType;

    private String addressLine;

    private Boolean isDefault;
}
