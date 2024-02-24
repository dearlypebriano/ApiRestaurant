package com.restaurant.apirestaurant.model;

import com.restaurant.apirestaurant.entity.Unit;
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
    private List<Unit> units;
    private String title;
    private BigDecimal rating;
    private BigDecimal price;
    private Integer qty;
    private String description;
    private List<CategoriesRequest> categories;
    private String details;
}
