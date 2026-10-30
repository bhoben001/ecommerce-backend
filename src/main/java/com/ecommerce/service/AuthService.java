package com.ecommerce.service;

import com.ecommerce.security.JwtUtil;
import com.ecommerce.dto.request.LoginRequest;
import com.ecommerce.dto.request.RegisterRequest;
import com.ecommerce.dto.response.AuthResponse;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    public UserResponse register(RegisterRequest request){

        String email = request.getEmail().trim().toLowerCase();

        if(userRepository.existsByEmail(email)) throw new ConflictException("Email already exists");
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        User response=userRepository.save(user);
        return userMapper.toResponse(response);

    }

    public AuthResponse login(LoginRequest request){

        String email = request.getEmail().trim().toLowerCase();

        User user=userRepository.findByEmail(email)
                .filter(User::getActive)
                .filter(u-> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(()->new UnauthorizedException("Invalid Email or Password"));
        return new AuthResponse(jwt.generateToken(user));
    }

}
