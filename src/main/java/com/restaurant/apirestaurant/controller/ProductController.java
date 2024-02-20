package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.model.ProductRequest;
import com.restaurant.apirestaurant.model.ProductResponse;
import com.restaurant.apirestaurant.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
     * Membuat produk baru.
     *
     * @param request Objek ProductRequest yang berisi informasi produk yang akan dibuat.
     * @param file    Berkas gambar untuk produk.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang berhasil dibuat.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @RequestParam ProductRequest request,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ProductResponse response = productService.createProduct(request, file);
        return ResponseEntity.created(null).body(response);
    }

    /**
     * Memperbarui produk yang ada.
     *
     * @param id      ID produk yang akan diperbarui.
     * @param request Objek ProductRequest yang berisi informasi produk yang baru.
     * @param file    Berkas gambar baru untuk produk (opsional).
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang berhasil diperbarui.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @PatchMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> update(
            @RequestParam String id,
            @RequestParam ProductRequest request,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ProductResponse response = productService.update(id, request, file);
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
    public ResponseEntity<ProductResponse> findByNameProduct(@RequestParam String nameProduct) {
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
    public ResponseEntity<List<ProductResponse>> findByPrice(@RequestParam BigDecimal price) {
        List<ProductResponse> response = productService.findByPrice(price);
        return ResponseEntity.ok().body(response);
    }
}