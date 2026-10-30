package com.ecommerce.mapper;

import com.ecommerce.dto.response.AddressResponse;
import com.ecommerce.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address){
        AddressResponse response=new AddressResponse();

        response.setId(address.getId());
        response.setUserId(address.getUser().getId());
        response.setFullName(address.getFullName());
        response.setPhone(address.getPhone());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setState(address.getState());
        response.setPinCode(address.getPinCode());
        response.setAddressType(address.getAddressType());
        response.setIsDefault(address.getIsDefault());
        response.setAddressLine(address.getAddressLine());

        return response;
    }

}
