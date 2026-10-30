package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.OrderStatusRequest;
import com.ecommerce.dto.request.PlaceOrderRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.entity.*;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    private static final BigDecimal FREE_SHIPPING_ABOVE = new BigDecimal("500");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("50");

    @Transactional
    public OrderResponse placeOrder(UserPrincipal me, PlaceOrderRequest request){
        Cart cart = cartRepository.findByUserId(me.getId())
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), me.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = new Order();
        order.setUser(userRepository.getReferenceById(me.getId()));
        order.setOrderNumber("ORD-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        order.setShippingFullName(address.getFullName());
        order.setShippingPhone(address.getPhone());
        order.setShippingAddressLine(address.getAddressLine());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPincode(address.getPinCode());

        BigDecimal subtotal = BigDecimal.ZERO;

        List<CartItem> cartItems = cart.getItems().stream()
                .sorted(Comparator.comparing(ci -> ci.getProduct().getId()))
                .toList();

        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();
            if (!Boolean.TRUE.equals(product.getActive()))
                throw new BadRequestException(product.getName() + " is no longer available");

            if (productRepository.adjustStock(product.getId(), -ci.getQuantity()) == 0)
                throw new InsufficientStockException("Not enough stock for " + product.getName());

            BigDecimal unitPrice = product.getDiscountPrice() != null
                    ? product.getDiscountPrice() : product.getPrice();

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitPrice(unitPrice);
            item.setQuantity(ci.getQuantity());
            item.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity())));
            order.addItem(item);

            subtotal = subtotal.add(item.getLineTotal());
        }

        BigDecimal shippingFee = subtotal.compareTo(FREE_SHIPPING_ABOVE) >= 0
                ? BigDecimal.ZERO : SHIPPING_FEE;

        order.setSubtotal(subtotal);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setShippingFee(shippingFee);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(shippingFee));

        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        return orderMapper.toResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(UserPrincipal me, Pageable pageable) {
        return orderRepository.findByUserId(me.getId(), pageable)
                .map(o -> orderMapper.toResponse(o, true));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status == null) ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(status, pageable);
        return orders.map(order -> orderMapper.toResponse(order, true));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UserPrincipal me, Long id) {
        return orderMapper.toResponse(findAccessible(me, id), true);
    }


    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));


        OrderStatus target = request.getStatus();
        boolean isNextStep = target != OrderStatus.CANCELLED && target.ordinal() == order.getStatus().ordinal() + 1;
        if (!isNextStep) {
            throw new BadRequestException("Cannot move order from " + order.getStatus() + " to " + target);
        }

        order.setStatus(target);
        return orderMapper.toResponse(order, false);
    }


    @Transactional
    public OrderResponse cancelOrder(UserPrincipal me, Long id) {
        Order order = findAccessible(me, id);

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled once it is " + order.getStatus());
        }

        for (OrderItem item : order.getItems()) {
            productRepository.adjustStock(item.getProduct().getId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(order, false);
    }


    private Order findAccessible(UserPrincipal me, Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean isAdmin = me.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getId().equals(me.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }
        return order;
    }

}
