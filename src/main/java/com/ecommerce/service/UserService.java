package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.UpdatePasswordRequest;
import com.ecommerce.dto.request.UserUpdateRequest;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse>  findAll(Pageable pageable) {
        Page<UserResponse> response= userRepository.findAll(pageable).map(userMapper::toResponse);
        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user= userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional()
    public UserResponse updateUser(UserPrincipal me, UserUpdateRequest request) {
        User user= userRepository.findById(me.getId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(request.getFullName()!=null) user.setFullName(request.getFullName().trim());
        if(request.getPhone()!=null) user.setPhone(request.getPhone());
        return userMapper.toResponse(user);
    }

    @Transactional
    public void updatePassword(UserPrincipal me, UpdatePasswordRequest request) {
        User user = userRepository.findById(me.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void softDelete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
    }

    @Transactional
    public void activeUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(true);
    }
}
