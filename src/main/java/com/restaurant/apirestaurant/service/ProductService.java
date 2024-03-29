package com.restaurant.apirestaurant.service;

import com.restaurant.apirestaurant.entity.Categories;
import com.restaurant.apirestaurant.entity.Product;
import com.restaurant.apirestaurant.entity.Unit;
import com.restaurant.apirestaurant.model.CategoriesRequest;
import com.restaurant.apirestaurant.model.ProductRequest;
import com.restaurant.apirestaurant.model.ProductResponse;
import com.restaurant.apirestaurant.repository.CategoriesRepository;
import com.restaurant.apirestaurant.repository.ProductRepository;
import com.restaurant.apirestaurant.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing products.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    private final String IMAGE_URL = "http://192.168.1.3:2000/api/images/";

    /**
     * Create a new product along with uploading an image.
     *
     * @param request The product details.
     * @param file    The image file to upload.
     * @return The created product response.
     * @throws RuntimeException if an error occurs while creating the product or uploading the image.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile file) throws IOException {
        byte[] imageDatas = file.getBytes();
        List<CategoriesRequest> categories = request.getCategories();
        List<Categories> categoryList = new ArrayList<>();
        for (CategoriesRequest categoriesRequest : categories) {
            Categories existingCategory = categoriesRepository.findByNameCategory(categoriesRequest.getNameCategory());
            if (existingCategory != null) {
                categoryList.add(existingCategory);
            } else {
                throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " tidak ditemukan!");
            }
        }

        Product product = Product.builder()
                        .title(request.getTitle())
                        .rating(request.getRating())
                        .price(request.getPrice())
                        .qty(request.getQty())
                        .description(request.getDescription())
                        .categories(categoryList)
                        .units(request.getUnits())
                        .details(request.getDetails())
                .build();

        String originalFileName = file.getOriginalFilename();
        String hashedFileName = ImageUtils.hashFileName(originalFileName);
        byte[] imageData = file.getBytes();
        Path imagePath = Paths.get(uploadPath, hashedFileName);
        Files.write(imagePath, imageData);

        product.setImageName(hashedFileName); // Simpan nama asli untuk referensi
        product.setImageType(file.getContentType());
        product.setImageData(imageDatas);
        product.setFilePath(IMAGE_URL + hashedFileName); // Simpan path lengkap dari file gambar

        // Simpan produk ke database
        productRepository.save(product);


        if (product == null) {
            throw new RuntimeException("Produk gagal dibuat, periksa kembali request yang anda masukkan!");
        }

        return toProductResponse(product);
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Path imagePath = Paths.get(uploadPath, fileName);
        return Files.readAllBytes(imagePath);
    }

    /**
     * Update an existing product along with updating the image.
     *
     * @param productId The ID of the product to update.
     * @param request   The updated product details.
     * @param file      The updated image file, if any.
     * @return The updated product response.
     * @throws RuntimeException if an error occurs while updating the product or uploading the image.
     */
    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest request, MultipartFile file) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product dengan ID : " + productId + " tidak ditemukan!"));

            if (file != null && !file.isEmpty()) {
                String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename());
                byte[] imageData = ImageUtils.compressImage(file.getBytes());

                product.setImageName(hashedFileName);
                product.setImageType(file.getContentType());
                product.setImageData(imageData);
            }

            if (request.getRating() != null) {
                product.setRating(request.getRating());
            }

            if (request.getUnits() != null) {
                product.setUnits(request.getUnits());
            }

            if (request.getTitle() != null) {
                product.setTitle(request.getTitle());
            }

            if (request.getPrice() != null) {
                product.setPrice(request.getPrice());
            }

            if (request.getQty() != null) {
                product.setQty(request.getQty());
            }

            if (request.getDescription() != null) {
                product.setDescription(request.getDescription());
            }

            if (request.getDetails() != null) {
                product.setDetails(request.getDetails());
            }

            List<CategoriesRequest> categoriesRequestList = request.getCategories();
            List<Categories> categoriesList = new ArrayList<>();
            for (CategoriesRequest categoriesRequest : categoriesRequestList) {
                Categories existingCategory = categoriesRepository.findByNameCategory(categoriesRequest.getNameCategory());
                if (existingCategory != null) {
                    categoriesList.add(existingCategory);
                } else {
                    throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " tidak ditemukan!");
                }
            }

            if (!categoriesList.isEmpty()) {
                product.setCategories(categoriesList);
            }

            productRepository.save(product);

            return toProductResponse(product);
        } catch (IOException e) {
            throw new RuntimeException("Gagal mengupdate produk: " + e.getMessage());
        }
    }

    /**
     *
     * @param id ini digunakan untuk menghapus data product berdasarkan ID Yang ada dalam database
     */
    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        productRepository.delete(product);
    }

    /**
     * Finds a product by its ID
     *
     * @param id the ID of the product to find
     * @return a response containing the product details
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(String id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        return toProductResponse(product);
    }

    /**
     * Finds all products with Pageable
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(this::toProductResponse);
    }

    /**
     * Finds a product by its name
     *
     * @param title
     * @return
     */
    @Transactional(readOnly = true)
    public ProductResponse findByTitle(String title) {
        Product product = productRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With Name : " + title + " Not Found!"));

        return toProductResponse(product);
    }

    /**
     * Finds a product by its price
     *
     * @param price
     * @return
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findByPrice(BigDecimal price) {
        List<Product> products = productRepository.findAllByPrice(price);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With Price : " + price + " Not Found!");
        }
        return products.stream().map(this::toProductResponse).toList();
    }

    /**
     * Converts a Product object to a ProductResponse object
     *
     * @param product
     * @return
     */
    private ProductResponse toProductResponse(Product product) {
        List<Categories> categories = product.getCategories();

        return ProductResponse.builder()
                .units(product.getUnits().stream().map(Unit::toString).toList())
                .id(product.getId())
                .title(product.getTitle())
                .rating(product.getRating())
                .price(product.getPrice())
                .qty(product.getQty())
                .description(product.getDescription())
                .categories(categories.stream().map(Categories::getNameCategory).toList())
                .details(product.getDetails())
                .imageName(product.getImageName())
                .imageType(product.getImageType())
                .imageData(product.getImageData()) // Tetap menyimpan imageData
                .filePath(product.getFilePath()) // Gunakan URL gambar
                .build();
    }
}