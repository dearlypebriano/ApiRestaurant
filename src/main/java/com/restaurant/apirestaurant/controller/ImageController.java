package com.restaurant.apirestaurant.controller;

import com.restaurant.apirestaurant.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {


    @Autowired
    private ProductService productService;

    /**
     *
     * @param imageName parameter ini digunakan untuk mencari gambar pada path berdasarkan nama file
     * @return
     * @throws IOException
     */
    @GetMapping("/fileSystem/{imageName}")
    public ResponseEntity<?> getImage(@PathVariable String imageName) throws IOException {
        byte[] imageData = productService.downloadImageFromFileSystem(imageName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
}