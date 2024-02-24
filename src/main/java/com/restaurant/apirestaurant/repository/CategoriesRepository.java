package com.restaurant.apirestaurant.repository;

import com.restaurant.apirestaurant.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    Categories findByNameCategory(String nameCategory);
}
