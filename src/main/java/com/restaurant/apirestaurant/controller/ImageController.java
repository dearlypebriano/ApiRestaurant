package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.entity.Product;
import com.restaurant.apirestaurant.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final String IMAGE_FOLDER = "C:\\Users\\dearl\\Documents\\ApiRestaurant\\src\\main\\resources\\static\\image\\upload\\";

    @Autowired
    private ProductService productService;

    /**
     * Mengambil gambar
     * 
     * @param imageName
     * @return
     * @throws IOException
     */
    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(IMAGE_FOLDER + imageName);
        Resource resource = new org.springframework.core.io.PathResource(imagePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Mengambil gambar berdasarkan ID
     *
     * @param imageId
     * @return
     */
    @GetMapping("/upload/{imageId}")
    public ResponseEntity<byte[]> getImageById(@PathVariable String imageId) {
        try {
            // Ambil data gambar dari ProductService
            byte[] imageData = productService.getImageDataById(imageId);

            // Jika gambar berhasil ditemukan, kembalikan sebagai respons
            if (imageData != null) {
                HttpHeaders headers = new HttpHeaders();

                // Tentukan jenis konten berdasarkan ekstensi file gambar
                String fileExtension = getFileExtension(imageId);
                if (fileExtension != null) {
                    switch (fileExtension.toLowerCase()) {
                        case "jpg":
                        case "jpeg":
                            headers.setContentType(MediaType.IMAGE_JPEG);
                            break;
                        case "png":
                            headers.setContentType(MediaType.IMAGE_PNG);
                            break;
                        // Tambahkan case untuk format gambar lainnya jika diperlukan
                        default:
                            // Jika format tidak dikenali, gunakan MIME tipe umum
                            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                            break;
                    }
                }

                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Jika gambar tidak ditemukan
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Jika terjadi kesalahan internal
        }
    }

    /**
     * Mengambil ekstensi file
     *
     * @param filename
     * @return
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return null;
    }
}
