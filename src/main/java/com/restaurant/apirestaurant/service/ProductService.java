package com.restaurant.apirestaurant.service;

import com.restaurant.apirestaurant.entity.Categories;
import com.restaurant.apirestaurant.entity.Product;
import com.restaurant.apirestaurant.model.CategoriesRequest;
import com.restaurant.apirestaurant.model.ProductRequest;
import com.restaurant.apirestaurant.model.ProductResponse;
import com.restaurant.apirestaurant.repository.CategoriesRepository;
import com.restaurant.apirestaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kelas ini menyediakan layanan untuk mengelola produk.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    /**
     * Membuat produk baru.
     *
     * @param request Objek ProductRequest yang berisi informasi produk yang akan dibuat.
     * @param file    Berkas gambar untuk produk.
     * @return Objek ProductResponse yang berisi informasi produk yang telah dibuat.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile file) throws IOException {
        String hashedFileName = hashFileName(file.getOriginalFilename());

        Product product = new Product();
        product.setNameProduct(request.getNameProduct());
        product.setPrice(request.getPrice());
        product.setQty(request.getQty());
        product.setDescription(request.getDescription());

        List<CategoriesRequest> categoriesRequestList = request.getCategories();
        List<Categories> categoriesList = new ArrayList<>();

        for (CategoriesRequest categoriesRequest : categoriesRequestList) {
            Categories existingCategory = categoriesRepository.findByNameCategory(categoriesRequest.getNameCategory());
            if (existingCategory != null) {
                categoriesList.add(existingCategory);
            } else {
                throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " not found!");
            }
        }
        product.setCategories(categoriesList);

        saveImage(file, hashedFileName);
        product.setImageName(hashedFileName);
        product.setImageType(file.getContentType());
        product.setImageData(file.getBytes());

        productRepository.save(product);

        return toProductResponse(product);
    }

    /**
     * Memperbarui produk yang ada.
     *
     * @param id      ID produk yang akan diperbarui.
     * @param request Objek ProductRequest yang berisi informasi produk yang baru.
     * @param file    Berkas gambar baru untuk produk (opsional).
     * @return Objek ProductResponse yang berisi informasi produk yang telah diperbarui.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @Transactional
    public ProductResponse update(String id, ProductRequest request, MultipartFile file) throws IOException {
        String hashedFileName = hashFileName(file.getOriginalFilename());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        if (product.getNameProduct() != null) {
            product.setNameProduct(request.getNameProduct());
        }

        if (product.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (product.getQty() != null) {
            product.setQty(request.getQty());
        }

        if (product.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        List<CategoriesRequest> categoriesRequestList = request.getCategories();
        List<Categories> categoriesList = new ArrayList<>();

        for (CategoriesRequest categoriesRequest : categoriesRequestList) {
            Categories existingCategory = categoriesRepository.findByNameCategory(categoriesRequest.getNameCategory());
            if (existingCategory != null) {
                categoriesList.add(existingCategory);
            } else {
                throw new RuntimeException("Category " + categoriesRequest.getNameCategory() + " not found!");
            }
        }

        if (product.getCategories() != null) {
            product.setCategories(categoriesList);
        }

        if (file != null) {
            saveImage(file, hashedFileName);
            product.setImageName(hashedFileName);
            product.setImageType(file.getContentType());
            product.setImageData(file.getBytes());
        }

        productRepository.save(product);

        return toProductResponse(product);
    }

    /**
     * Menghapus produk berdasarkan ID.
     *
     * @param id ID produk yang akan dihapus.
     */
    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        productRepository.delete(product);
    }

    /**
     * Mencari produk berdasarkan ID.
     *
     * @param id ID produk yang akan dicari.
     * @return Objek ProductResponse yang berisi informasi produk yang ditemukan.
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        return toProductResponse(product);
    }

    /**
     * Mengambil daftar produk dengan pembatasan halaman dan jumlah item per halaman.
     *
     * @param page Nomor halaman (dimulai dari 0).
     * @param size Jumlah item per halaman.
     * @return Halaman dari daftar produk.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(this::toProductResponse);
    }

    /**
     * Mencari produk berdasarkan nama produk.
     *
     * @param nameProduct Nama produk yang akan dicari.
     * @return Objek ProductResponse yang berisi informasi produk yang ditemukan.
     */
    @Transactional(readOnly = true)
    public ProductResponse findByNameProduct(String nameProduct) {
        Product product = productRepository.findByNameProduct(nameProduct)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With Name : " + nameProduct + " Not Found!"));

        return toProductResponse(product);
    }

    /**
     * Mencari produk berdasarkan harga.
     *
     * @param price Harga produk yang akan dicari.
     * @return Daftar objek ProductResponse yang berisi informasi produk yang ditemukan.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findByPrice(BigDecimal price) {
        List<Product> products = productRepository.findAllByPrice(price);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Products With Price : " + price + " Not Found!");
        }
        return products.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    /**
     * Menghasilkan hash dari nama file gambar.
     *
     * @param originalFileName Nama asli file gambar.
     * @return Nama file gambar yang telah di-hash.
     */
    private String hashFileName(String originalFileName) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(originalFileName.getBytes());
            byte[] digest = md.digest();
            StringBuilder hashedFileName = new StringBuilder();
            for (byte b : digest) {
                hashedFileName.append(String.format("%02x", b));
            }
            return hashedFileName.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Menyimpan gambar ke folder yang ditentukan.
     *
     * @param file          Berkas gambar.
     * @param hashedFileName Nama file gambar yang telah di-hash.
     * @throws IOException jika terjadi kesalahan saat menyimpan gambar.
     */
    private void saveImage(MultipartFile file, String hashedFileName) throws IOException {
        String folderPath = "C:\\Users\\dearl\\Documents\\ApiRestaurant\\src\\main\\resources\\static\\image\\upload\\";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs(); // Buat direktori jika belum ada
        }

        File imageFile = new File(folder, hashedFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(file.getBytes());
        }
    }

    /**
     * Mengonversi objek Product menjadi objek ProductResponse.
     *
     * @param product Objek Product yang akan dikonversi.
     * @return Objek ProductResponse yang dihasilkan.
     */
    private ProductResponse toProductResponse(Product product) {
        List<Categories> categories = product.getCategories();

        return ProductResponse.builder()
                .id(product.getId())
                .nameProduct(product.getNameProduct())
                .price(product.getPrice())
                .qty(product.getQty())
                .description(product.getDescription())
                .categories(categories.stream().map(Categories::getNameCategory).toList())
                .imageName(product.getImageName())
                .imageType(product.getImageType())
                .imageData(product.getImageData())
                .build();
    }
}