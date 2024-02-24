package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.model.OrderRequest;
import com.restaurant.apirestaurant.model.OrderResponse;
import com.restaurant.apirestaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderServicer;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderResponse> create(
            @RequestParam String customerName,
            @RequestParam String address,
            @RequestParam List<String> productNames
    ) {
        OrderRequest request = new OrderRequest();
        request.setCustomerName(customerName);
        request.setAddress(address);
        request.setProductNames(productNames);

        OrderResponse response = orderServicer.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(path = "update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderResponse> update(
            @PathVariable String id,
            @RequestParam String customerName,
            @RequestParam String address,
            @RequestParam List<String> productNames
    ) {
        OrderRequest request = new OrderRequest();
        request.setCustomerName(customerName);
        request.setAddress(address);
        request.setProductNames(productNames);

        OrderResponse response = orderServicer.updateOrder(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderServicer.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> get(@PathVariable String id) {
        OrderResponse response = orderServicer.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> response = orderServicer.findAll(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
