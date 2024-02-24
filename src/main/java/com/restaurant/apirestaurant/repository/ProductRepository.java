package com.restaurant.apirestaurant.repository;

import com.restaurant.apirestaurant.entity.Categories;
import com.restaurant.apirestaurant.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     *
     * @param title
     * @return
     */
    Optional<Product> findByTitle(String title);

    /**
     *
     * @param price
     * @return
     */
    List<Product> findAllByPrice(BigDecimal price);

    /**
     * @param imageName
     * @return
     */
    Optional<Product> findByImageName(String imageName);

    /**
     *
     * @param categories
     * @return
     */
    List<Product> findAllByCategories(List<Categories> categories);

    /**
     *
     * @param productNames
     * @return
     */
    List<Product> findByTitleIn(List<String> productNames);
}
