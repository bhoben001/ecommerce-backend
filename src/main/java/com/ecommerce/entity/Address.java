package com.ecommerce.entity;

import com.ecommerce.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String state;

    @Size(min = 6,max = 6,message = "Pin code must be exactly 6 digits")
    @Column(nullable = false)
    private String pinCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private Boolean isDefault=false;

    @Column(nullable = false)
    private String addressLine;
    }
