package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.model.CategoriesRequest;
import com.restaurant.apirestaurant.model.CategoriesResponse;
import com.restaurant.apirestaurant.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    /**
     * @path /api/categories/create - POST (Menambahkan data kategori berdasarkan input nameCategory (nama category) berdasarkan input Admin
     * @part request
     * @return
     */
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesResponse> create(@RequestBody CategoriesRequest request) {
        CategoriesResponse categoriesResponse = categoriesService.createCategories(request);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.CREATED);
    }

    /**
     * Deskripsi dari seluruh fungsi Java.
     *
     * @param  id             deskripsi dari parameter
     * @param  nameCategory   deskripsi dari parameter
     * @return                deskripsi dari nilai kembalian
     */
    @PatchMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesResponse> update(@PathVariable Long id, @RequestParam CategoriesRequest nameCategory) {
        CategoriesResponse categoriesResponse = categoriesService.updateCategories(id, nameCategory);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }

    /**
     * Menghapus kategori berdasarkan ID.
     *
     * @param  id   ID kategori yang akan dihapus
     * @return      respons entitas dengan pesan yang mengkonfirmasi penghapusan
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoriesService.deleteCategories(id);
        return new ResponseEntity<>("Categories deleted successfully", HttpStatus.OK);
    }

    /**
     * Mendapatkan kategori berdasarkan ID.
     *
     * @param  id   ID dari kategori
     * @return      respons entitas yang berisi respons kategori
     */
    @GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesResponse> get(@PathVariable Long id) {
        CategoriesResponse categoriesResponse = categoriesService.getCategoriesById(id);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }

    /**
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CategoriesResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoriesResponse> categoriesResponse = categoriesService.list(page, size);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }
}
