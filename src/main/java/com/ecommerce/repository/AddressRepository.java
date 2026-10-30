package com.ecommerce.repository;

import com.ecommerce.entity.Address;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Page<Address> findByUserId(Long userId, Pageable pageable);

    Optional<Address> findByIdAndUserId(@NotNull(message = "addressId is required") Long addressId, Long id);
}
