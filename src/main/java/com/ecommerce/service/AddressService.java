package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.AddressRequest;
import com.ecommerce.dto.request.AddressUpdateRequest;
import com.ecommerce.dto.response.AddressResponse;
import com.ecommerce.entity.Address;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressResponse createAddress(UserPrincipal me, AddressRequest request){
            Address address=new Address();
            User user=userRepository.findById(me.getId()).orElseThrow(()->
                    new ResourceNotFoundException("user not found"));

            address.setUser(user);
            address.setFullName(request.getFullName());
            address.setCity(request.getCity());
            address.setDistrict(request.getDistrict());
            address.setState(request.getState());
            address.setPinCode(request.getPinCode());
            address.setPhone(request.getPhone());
            address.setAddressType(request.getAddressType());
            address.setAddressLine(request.getAddressLine());

            List<Address> addresses =addressRepository.findByUserId(me.getId());

            if(addresses.size()==0)address.setIsDefault(TRUE);

            Address saved =addressRepository.save(address);
            return addressMapper.toResponse(saved);
    }

    @Transactional
    public AddressResponse updateAddress(UserPrincipal me, Long addressId, AddressUpdateRequest request){

        Address address=addressRepository.findByIdAndUserId(addressId,me.getId()).orElseThrow(()->
                new ResourceNotFoundException("address not found"));

        if(request.getFullName()!=null) address.setFullName(request.getFullName());
        if(request.getCity()!=null) address.setCity(request.getCity());
        if(request.getDistrict()!=null) address.setDistrict(request.getDistrict());
        if(request.getState()!=null) address.setState(request.getState());
        if(request.getPinCode()!=null)  address.setPinCode(request.getPinCode());
        if(request.getPhone()!=null) address.setPhone(request.getPhone());
        if(request.getAddressType()!=null) address.setAddressType(request.getAddressType());
        if(request.getAddressLine()!=null) address.setAddressLine(request.getAddressLine());

        return addressMapper.toResponse(address);
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> list(UserPrincipal me, Pageable pageable){
        Page<Address> result= addressRepository.findByUserId(me.getId(),pageable);
        return result.map(addressMapper::toResponse);
    }

    @Transactional
    public void markDefault(UserPrincipal me, Long addressId){

        List<Address> userAddresses = addressRepository.findByUserId(me.getId());

        if (userAddresses.stream().noneMatch(a -> a.getId().equals(addressId)))
            throw new ResourceNotFoundException("address not found");

        for (Address address : userAddresses) {
            address.setIsDefault(address.getId().equals(addressId));
        }
    }

    public void deleteAddress(UserPrincipal me, Long addressId){
        Address address =addressRepository.findByIdAndUserId(addressId,me.getId())
                .orElseThrow(()-> new ResourceNotFoundException("address not found"));
        addressRepository.delete(address);
    }

}