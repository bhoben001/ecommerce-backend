package com.ecommerce.controller;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.UpdatePasswordRequest;
import com.ecommerce.dto.request.UserUpdateRequest;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ---------- user----------

    @GetMapping("/me")
    public UserResponse getMyProfile(@AuthenticationPrincipal UserPrincipal me) {
        return userService.findById(me.getId());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@AuthenticationPrincipal UserPrincipal me,
                                        @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(me, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@AuthenticationPrincipal UserPrincipal me,
                               @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(me, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateMyAccount(@AuthenticationPrincipal UserPrincipal me) {
        userService.softDelete(me.getId());
    }

    // ---------- only admin ----------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(@PageableDefault(size = 10, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable Long id) {
        userService.softDelete(id);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable Long id) {
        userService.activeUser(id);
    }
}
