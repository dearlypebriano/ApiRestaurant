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
public class ProductResponse {

    private String id;
    private String nameProduct;
    private BigDecimal price;
    private Integer qty;
    private String description;
    private List<String> categories;
    private String imageName;
    private String imageType;
    private byte[] imageData;
}
