package com.restaurant.apirestaurant.service;

import com.restaurant.apirestaurant.entity.Categories;
import com.restaurant.apirestaurant.entity.Product;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Create a new product along with uploading an image.
     * @param request The product details.
     * @param file The image file to upload.
     * @return The created product response.
     * @throws RuntimeException if an error occurs while creating the product or uploading the image.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile file) {
        try {
            String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename());
            byte[] imageData = ImageUtils.compressImage(file.getBytes());
            Blob imageDataBlob = ImageUtils.byteArrayToBlob(imageData);

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
                    throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " tidak ditemukan!");
                }
            }
            product.setCategories(categoriesList);

            product.setImageName(hashedFileName);
            product.setImageType(file.getContentType());
            product.setImageData(imageDataBlob);

            productRepository.save(product);

            return toProductResponse(product);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Gagal menyimpan produk: " + e.getMessage());
        }
    }

    /**
     * Update an existing product along with updating the image.
     * @param productId The ID of the product to update.
     * @param request The updated product details.
     * @param file The updated image file, if any.
     * @return The updated product response.
     * @throws RuntimeException if an error occurs while updating the product or uploading the image.
     */
    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest request, MultipartFile file) {
        try {
            String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename());
            byte[] imageData = ImageUtils.compressImage(file.getBytes());
            Blob imageDataBlob = ImageUtils.byteArrayToBlob(imageData);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product dengan ID : " + productId + " tidak ditemukan!"));

            if (file != null && !file.isEmpty()) {
                hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename());
                imageData = ImageUtils.compressImage(file.getBytes());

                product.setImageName(hashedFileName);
                product.setImageType(file.getContentType());
                product.setImageData(imageDataBlob);
            }

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
                    throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " tidak ditemukan!");
                }
            }
            product.setCategories(categoriesList);

            productRepository.save(product);

            return toProductResponse(product);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Gagal mengupdate produk: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With ID : " + id + " Not Found!"));

        productRepository.delete(product);
    }

    /**
     * Finds a product by its ID
     *
     * @param  id   the ID of the product to find
     * @return      a response containing the product details
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
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
     * @param nameProduct
     * @return
     */
    @Transactional(readOnly = true)
    public ProductResponse findByNameProduct(String nameProduct) {
        Product product = productRepository.findByNameProduct(nameProduct)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With Name : " + nameProduct + " Not Found!"));

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Products With Price : " + price + " Not Found!");
        }
        return products.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    private String saveImageAndGetUrl(String imageName, Blob imageData) throws IOException {
        String folderPath = "C:\\Users\\dearl\\Documents\\ApiRestaurant\\src\\main\\resources\\static\\image\\upload\\";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Buat nama file yang unik dengan UUID
        String uniqueFileName = UUID.randomUUID().toString();
        String filePath = folderPath + uniqueFileName;

        // Konversi blob menjadi byte array
        byte[] imageBytes;
        try (InputStream inputStream = imageData.getBinaryStream()) {
            imageBytes = inputStream.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to read image data from Blob", e);
        }

        // Simpan byte array sebagai file gambar
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save image file", e);
        }

        // Kembalikan URL gambar
        return "http://0.0.0.0:2000/image/upload/" + uniqueFileName;
    }

    private byte[] blobToByteArray(Blob blob) throws SQLException {
        try (InputStream inputStream = blob.getBinaryStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLException("Failed to convert Blob to byte array", e);
        }
    }

    /**
     * Converts a Product object to a ProductResponse object
     *
     * @param product
     * @return
     */
    private ProductResponse toProductResponse(Product product) {
        List<Categories> categories = product.getCategories();
        String imageUrl = null;
        byte[] imageDataBytes = null;

        try {
            imageUrl = saveImageAndGetUrl(product.getImageName(), product.getImageData());
            imageDataBytes = blobToByteArray(product.getImageData());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .nameProduct(product.getNameProduct())
                .price(product.getPrice())
                .qty(product.getQty())
                .description(product.getDescription())
                .categories(categories.stream().map(Categories::getNameCategory).toList())
                .imageName(product.getImageName())
                .imageType(product.getImageType())
                .imageData(imageDataBytes) // Tetap menyimpan imageData
                .imageUrl(imageUrl) // Gunakan URL gambar
                .build();
    }
}
