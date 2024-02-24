package com.restaurant.apirestaurant.service;

import com.restaurant.apirestaurant.entity.Order;
import com.restaurant.apirestaurant.entity.Product;
import com.restaurant.apirestaurant.model.OrderRequest;
import com.restaurant.apirestaurant.model.OrderResponse;
import com.restaurant.apirestaurant.repository.OrderRepository;
import com.restaurant.apirestaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setAddress(request.getAddress());

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Product> orderedProduct = new ArrayList<>();
        for (String productName : request.getProductNames()) {
            Optional<Product> optionalProduct = productRepository.findByTitle(productName);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                totalPrice = totalPrice.add(product.getPrice());
                order.getProducts().add(product);
            } else {
                throw new RuntimeException("Product with name : " + productName + " Not Found!");
            }
        }
        order.setProducts(orderedProduct);
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrder(String orderId, OrderRequest request) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found"));

        if (request.getCustomerName() != null) {
            existingOrder.setCustomerName(request.getCustomerName());
        }

        if (request.getAddress() != null) {
            existingOrder.setAddress(request.getAddress());
        }

        if (request.getProductNames() != null && !request.getProductNames().isEmpty()) {

            List<Product> updatedProducts = productRepository.findByTitleIn(request.getProductNames());
            existingOrder.setProducts(updatedProducts);

            BigDecimal totalPrice = updatedProducts.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existingOrder.setTotalPrice(totalPrice);
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found"));

        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found"));

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(this::mapToOrderResponse);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<String> productName = order.getProducts().stream()
                .map(Product::getTitle)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .address(order.getAddress())
                .orderDate(order.getOrderDate())
                .price(order.getTotalPrice())
                .products(productName)
                .build();
    }
}
