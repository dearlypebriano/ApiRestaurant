package com.restaurant.apirestaurant.controller;

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
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

/**
 * Kelas ini bertanggung jawab untuk menangani permintaan terkait produk.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Membuat produk baru dengan informasi yang diberikan dan menyimpannya ke dalam sistem.
     *
     * @param nameProduct   Nama produk.
     * @param price         Harga produk.
     * @param qty           Jumlah produk yang tersedia.
     * @param description   Deskripsi produk.
     * @param categories    Daftar kategori produk.
     * @param file          File gambar produk.
     * @return Objek ResponseEntity yang berisi informasi produk yang baru dibuat.
     * @throws IOException jika terjadi kesalahan saat memproses file.
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @RequestParam String nameProduct,
            @RequestParam BigDecimal price,
            @RequestParam Integer qty,
            @RequestParam String description,
            @RequestParam List<String> categories,
            @RequestPart("file") MultipartFile file
    ) throws IOException, SQLException {

        fileStorageService.saveImageToServer(file);

        // Salin atribut lainnya
        ProductRequest request = new ProductRequest();
        request.setNameProduct(nameProduct);
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
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Memperbarui produk yang ada.
     *
     * @param id          ID produk yang akan diperbarui.
     * @param nameProduct Nama baru untuk produk.
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
            @RequestParam String id,
            @RequestParam String nameProduct,
            @RequestParam BigDecimal price,
            @RequestParam Integer qty,
            @RequestParam String description,
            @RequestParam List<String> categories,
            @RequestPart("file") MultipartFile file
    ) throws IOException, SQLException {

        fileStorageService.saveImageToServer(file);

        ProductRequest request = new ProductRequest();
        request.setNameProduct(nameProduct);
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
    public ResponseEntity<ProductResponse> findById(@RequestParam String id) {
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
     * @param nameProduct Nama produk yang akan ditemukan.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang ditemukan.
     */
    @GetMapping(path = "/find/{nameProduct}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> findByNameProduct(@PathVariable String nameProduct) {
        ProductResponse response = productService.findByNameProduct(nameProduct);
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