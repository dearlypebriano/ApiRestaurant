package com.restaurant.apirestaurant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private String nameProduct;
    private BigDecimal price;
    private Integer qty;
    private String description;
    private List<CategoriesRequest> categories;
}
