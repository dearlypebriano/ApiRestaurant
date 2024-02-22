package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.entity.Unit;
import com.restaurant.apirestaurant.model.CategoriesRequest;
import com.restaurant.apirestaurant.model.ProductRequest;
import com.restaurant.apirestaurant.model.ProductResponse;
import com.restaurant.apirestaurant.service.FileStorageService;
import com.restaurant.apirestaurant.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Kelas ini bertanggung jawab untuk menangani permintaan terkait produk.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Metode ini digunakan untuk membuat produk baru
     * @param units parameters ini digunakan untuk menentukan unit produk
     * @param title parameters ini digunakan untuk menentukan judul produk
     * @param rating parameters ini digunakan untuk menentukan rating
     * @param price parameters ini digunakan untuk menentukan harga produk
     * @param qty parameters ini digunakan untuk menentukan quantity
     * @param description parameters ini digunakan untuk menentukan deskripsi
     * @param categories parameters ini digunakan untuk menentukan kategori
     * @param file parameters ini digunakan untuk menentukan gambar
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @RequestParam List<String> units,
            @RequestParam String title,
            @RequestParam BigDecimal rating,
            @RequestParam BigDecimal price,
            @RequestParam Integer qty,
            @RequestParam String description,
            @RequestParam List<String> categories,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ProductRequest request = new ProductRequest();
        List<Unit> unitsList = units.stream().map(Unit::valueOf).toList();
        request.setUnits(unitsList);
        request.setTitle(title);
        request.setRating(rating);
        request.setPrice(price);
        request.setQty(qty);
        request.setDescription(description);

        List<CategoriesRequest> categoriesRequestList = categories.stream()
                .map(categoryName -> {
                    CategoriesRequest categoryRequest = new CategoriesRequest();
                    categoryRequest.setNameCategory(categoryName);
                    return categoryRequest;
                })
                .toList();

        request.setCategories(categoriesRequestList);

        ProductResponse response = productService.createProduct(request, file);
        String imageName = response.getImageName();
        String imageUrl = "http://192.168.1.3:2000/api/images/" + imageName;
        response.setFilePath(imageUrl);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Memperbarui produk yang ada.
     *
     * @param units       Daftar unit yang diperbarui
     * @param id          ID produk yang akan diperbarui.
     * @param title       Title baru untuk produk.
     * @param rating      Rating baru untuk produk.
     * @param price       Harga baru untuk produk.
     * @param qty         Jumlah baru untuk produk.
     * @param description Deskripsi baru untuk produk.
     * @param categories  Daftar kategori baru untuk produk.
     * @param file        Berkas gambar baru untuk produk (opsional).
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang berhasil diperbarui.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @PatchMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> update(
            @RequestParam List<String> units,
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam BigDecimal rating,
            @RequestParam BigDecimal price,
            @RequestParam Integer qty,
            @RequestParam String description,
            @RequestParam List<String> categories,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ProductRequest request = new ProductRequest();
        List<Unit> unitsList = units.stream().map(Unit::valueOf).toList();
        request.setUnits(unitsList);
        request.setTitle(title);
        request.setRating(rating);
        request.setPrice(price);
        request.setQty(qty);
        request.setDescription(description);

        List<CategoriesRequest> categoriesRequestList = categories.stream()
                .map(categoryName -> {
                    CategoriesRequest categoryRequest = new CategoriesRequest();
                    categoryRequest.setNameCategory(categoryName);
                    return categoryRequest;
                })
                .toList();

        request.setCategories(categoriesRequestList);

        ProductResponse response = productService.updateProduct(id, request, file);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Menghapus produk berdasarkan ID.
     *
     * @param id ID produk yang akan dihapus.
     * @return ResponseEntity tanpa isi jika produk berhasil dihapus.
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@RequestParam String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Menemukan produk berdasarkan ID.
     *
     * @param id ID produk yang akan ditemukan.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang ditemukan.
     */
    @GetMapping(path = "/find/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> findById(@RequestParam String id) throws IOException {
        ProductResponse response = productService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Mendapatkan daftar produk dengan pembatasan halaman dan ukuran halaman.
     *
     * @param page Nomor halaman (dimulai dari 0).
     * @param size Jumlah produk per halaman.
     * @return ResponseEntity yang berisi halaman dari daftar produk.
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ProductResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> response = productService.findAll(page, size);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Menemukan produk berdasarkan nama produk.
     *
     * @param title Judul produk yang akan ditemukan.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang ditemukan.
     */
    @GetMapping(path = "/find/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> findByTitle(@PathVariable String title) {
        ProductResponse response = productService.findByTitle(title);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Menemukan produk berdasarkan harga.
     *
     * @param price Harga produk yang akan dicari.
     * @return ResponseEntity yang berisi daftar objek ProductResponse dari produk dengan harga yang sesuai.
     */
    @GetMapping(path = "/find/{price}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> findByPrice(@PathVariable BigDecimal price) {
        List<ProductResponse> response = productService.findByPrice(price);
        return ResponseEntity.ok().body(response);
    }
}