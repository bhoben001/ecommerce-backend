package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.AddressRequest;
import com.ecommerce.dto.request.AddressUpdateRequest;
import com.ecommerce.dto.response.AddressResponse;
import com.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse createAddress(@AuthenticationPrincipal UserPrincipal me,
                                         @Valid @RequestBody AddressRequest address){
        return addressService.createAddress(me, address);
    }

    @PutMapping("/{id}")
    public AddressResponse updateAddress(@AuthenticationPrincipal UserPrincipal me,
                                         @Valid @RequestBody AddressUpdateRequest address, @PathVariable Long id){
        return addressService.updateAddress(me,id,address);
    }

    @GetMapping()
    public Page<AddressResponse> getAllAddress(@AuthenticationPrincipal UserPrincipal me, @PageableDefault Pageable pageable) {
        return addressService.list(me, pageable);
    }


    @PatchMapping("/{id}/default")
    public void markDefault(@AuthenticationPrincipal UserPrincipal me,@PathVariable Long id){
        addressService.markDefault(me,id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long id){
        addressService.deleteAddress(me,id);
    }

}
