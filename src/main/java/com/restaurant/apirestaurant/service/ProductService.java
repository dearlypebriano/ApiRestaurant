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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing products.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Value("${image.upload.path}") // Path folder tempat gambar disimpan
    private String imageUploadPath;

    @Value("${image.endpoint.url}") // URL endpoint gambar
    private String imageEndpointUrl;

    private final String FOLDER_PATH = "C:\\Users\\dearl\\Documents\\ApiRestaurant\\src\\main\\resources\\static\\image\\upload\\";

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
        String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename());
        String filePath = FOLDER_PATH + hashedFileName;

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

        byte[] imageData = ImageUtils.compressImage(file.getBytes());

        Product product = productRepository.save(Product.builder()
                        .title(request.getTitle())
                        .rating(request.getRating())
                        .price(request.getPrice())
                        .qty(request.getQty())
                        .description(request.getDescription())
                        .categories(categoryList)
                        .units(request.getUnits())
                        .imageName(hashedFileName)
                        .imageType(file.getContentType())
                        .imageData(imageData)
                        .filePath(filePath)
                .build());

        if (product != null) {
            uploadImageToFileSystem(imageData, hashedFileName);
        } else {
            throw new RuntimeException("Gagal menyimpan data produk, cek kembali apakah input yang anda masukkan sudah sesuai!");
        }

        return toProductResponse(product);
    }

    /**
     *
     * @param imageName
     * @return
     * @throws IOException
     */
    public byte[] downloadImageFromFileSystem(String imageName) throws IOException {
        Optional<Product> fileData = productRepository.findByImageName(imageName);
        String filePath = fileData.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }

    /**
     *
     * @param imageData
     * @param imageName
     * @return
     * @throws IOException
     */
    public String uploadImageToFileSystem(byte[] imageData, String imageName) throws IOException {
        String hashedName = ImageUtils.hashFileName(imageName);
        String filePath = FOLDER_PATH + hashedName;

        Product product = productRepository.save(Product.builder()
                .imageName(hashedName)
                .imageType("image/png")
                .imageData(ImageUtils.compressImage(imageData))
                .filePath(filePath)
                .build());

        if (product != null) {
            return "File uploaded successfully" + hashedName;
        } else {
            return null;
        }
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
     * @param imageId
     * @return
     */
    @Transactional(readOnly = true)
    public byte[] getImageDataById(String imageId) {
        Optional<Product> product = productRepository.findByImageName(imageId);

        if (product != null) {
            product.get().getImageName();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image With Name: " + imageId + " Not Found!");
        }

        return product.get().getImageData();
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
                .imageName(product.getImageName())
                .imageType(product.getImageType())
                .imageData(product.getImageData()) // Tetap menyimpan imageData
                .filePath(product.getFilePath()) // Gunakan URL gambar
                .build();
    }
}