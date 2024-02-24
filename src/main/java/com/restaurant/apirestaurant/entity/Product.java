package com.restaurant.apirestaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product {

    @ElementCollection(targetClass = Unit.class)
    @Enumerated(EnumType.STRING)
    private List<Unit> units;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "name_product")
    private String title;

    private BigDecimal rating;

    private BigDecimal price;

    private Integer qty;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Categories> categories;

    private String details;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_type")
    private String imageType;

    @Lob
    @Column(name = "image_data", length = 1000)
    private byte[] imageData;

    @Column(name = "image_path")
    private String filePath;
}
