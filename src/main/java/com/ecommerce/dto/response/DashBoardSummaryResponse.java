package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashBoardSummaryResponse {
    long totalUsers;
    long totalProducts;
    long totalOrders;
    long pendingOrders;
    BigDecimal totalRevenue;
}
