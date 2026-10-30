package com.ecommerce.controller;

import com.ecommerce.dto.response.DashBoardSummaryResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.TopProductResponse;
import com.ecommerce.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashBoardController {

    private final DashBoardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public DashBoardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProductResponse> getLowStock(@RequestParam(defaultValue = "10") int threshold,
                                             @PageableDefault(size = 20, sort = "stockQuantity") Pageable pageable) {
        return dashboardService.getLowStockProducts(threshold, pageable);
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopProductResponse> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getTopProducts(limit);
    }
}
