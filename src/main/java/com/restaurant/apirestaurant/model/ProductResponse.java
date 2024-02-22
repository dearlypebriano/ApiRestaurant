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
    private List<String> units;
    private String id;
    private String title;
    private BigDecimal rating;
    private BigDecimal price;
    private Integer qty;
    private String description;
    private List<String> categories;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    private String filePath;
}
