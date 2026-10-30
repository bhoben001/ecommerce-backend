package com.ecommerce.service;

import com.ecommerce.dto.response.DashBoardSummaryResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.TopProductResponse;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.Role;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public DashBoardSummaryResponse getSummary() {
        long totalUsers=userRepository.countByRole(Role.CUSTOMER);
        long totalProduct=productRepository.countByActiveTrue();
        long totalOrders = orderRepository.countByStatusNot(OrderStatus.CANCELLED);
        long pendingOrders=orderRepository.countByStatus(OrderStatus.PENDING);
        BigDecimal totalRevenue=orderRepository.sumRevenueExcluding(OrderStatus.CANCELLED);

        DashBoardSummaryResponse response = new DashBoardSummaryResponse();
        response.setTotalUsers(totalUsers);
        response.setTotalProducts(totalProduct);
        response.setTotalOrders(totalOrders);
        response.setPendingOrders(pendingOrders);
        response.setTotalRevenue(totalRevenue == null ? BigDecimal.ZERO : totalRevenue);

        return response;
    }

    public Page<ProductResponse> getLowStockProducts( int threshold, Pageable pageable) {
        return productRepository.findByActiveTrueAndStockQuantityLessThanEqual(threshold, pageable)
                .map(p -> productMapper.toResponse(p, false));
    }

    public List<TopProductResponse> getTopProducts(int limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        return orderItemRepository.findTopProducts(OrderStatus.CANCELLED, PageRequest.of(0, size));
    }
}
