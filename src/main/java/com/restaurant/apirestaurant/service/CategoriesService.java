package com.restaurant.apirestaurant.service;

import com.restaurant.apirestaurant.entity.Categories;
import com.restaurant.apirestaurant.model.CategoriesRequest;
import com.restaurant.apirestaurant.model.CategoriesResponse;
import com.restaurant.apirestaurant.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    /**
     * Membuat kategori baru berdasarkan permintaan yang diberikan.
     *
     * @param  categoriesRequest  objek permintaan yang berisi nama kategori
     * @return                   objek respons yang mewakili kategori yang dibuat
     * */
    @Transactional
    public CategoriesResponse createCategories(CategoriesRequest categoriesRequest) {
        Categories categories = new Categories();
        categories.setNameCategory(categoriesRequest.getNameCategory());

        Categories savedCategories = categoriesRepository.save(categories);
        return toCategoriesResponse(savedCategories);
    }

    @Transactional
    public CategoriesResponse updateCategories(Long id, CategoriesRequest categoriesRequest) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        if (categories.getNameCategory() != null) {
            categories.setNameCategory(categoriesRequest.getNameCategory());

        }

        Categories updatedCategories = categoriesRepository.save(categories);
        return toCategoriesResponse(updatedCategories);
    }

    @Transactional
    public void deleteCategories(Long id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);

        categoriesRepository.delete(categories);
    }

    @Transactional
    public CategoriesResponse getCategoriesById(Long id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        return toCategoriesResponse(categories);
    }

    @Transactional(readOnly = true)
    public Page<CategoriesResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Categories> categoriesPage = categoriesRepository.findAll(pageable);
        return categoriesPage.map(this::toCategoriesResponse);
    }


    private CategoriesResponse toCategoriesResponse(Categories categories) {
        return CategoriesResponse.builder()
                .id(categories.getId())
                .nameCategory(categories.getNameCategory())
                .build();
    }
}
