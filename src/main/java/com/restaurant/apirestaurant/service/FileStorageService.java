package com.restaurant.apirestaurant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileStorageService {

    private static final String UPLOAD_FOLDER = "C:/Users/dearl/Documents/ApiRestaurant/src/main/resources/static/images/upload/";

    public void saveImageToServer(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File uploadDir = new File(UPLOAD_FOLDER);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File imageFile = new File(uploadDir + fileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(file.getBytes());
        }
    }
}
