package com.restaurant.apirestaurant.repository;

import com.restaurant.apirestaurant.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByNameProduct(String nameProduct);

    Optional<Product> findByPrice(BigDecimal price);
}
