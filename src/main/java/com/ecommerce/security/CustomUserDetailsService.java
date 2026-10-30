package com.ecommerce.security;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        UserPrincipal userPrincipal=new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setFullName(user.getFullName());
        userPrincipal.setEmail(user.getEmail());
        userPrincipal.setRole(user.getRole());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setEnabled(Boolean.TRUE.equals(user.getActive()));

        return userPrincipal;

    }
}
