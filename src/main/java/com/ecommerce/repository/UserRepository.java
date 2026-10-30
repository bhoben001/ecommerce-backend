package com.ecommerce.repository;

import com.ecommerce.entity.User;
import com.ecommerce.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String Email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
}
