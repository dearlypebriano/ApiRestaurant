package com.restaurant.apirestaurant.util;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {

    /**
     * Mengompresi gambar menjadi array byte[]
     * @param data Data dari gambar dalam bentuk array byte[]
     * @return Data gambar yang telah dikompresi dalam bentuk array byte[]
     */
    public static byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    /**
     * Mendekompresi gambar dari array byte[]
     * @param data Data gambar yang dikompresi dalam bentuk array byte[]
     * @return Data gambar yang telah dikompresi dalam bentuk array byte[]
     */
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    /**
     * Generates a hashed file name based on the original file name.
     * @param originalFileName The original file name.
     * @return The hashed file name.
     */
    public static String hashFileName(String originalFileName) {
        try {
            String extension = "";
            int lastIndexOfDot = originalFileName.lastIndexOf('.');
            if (lastIndexOfDot > 0) {
                extension = originalFileName.substring(lastIndexOfDot);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(originalFileName.getBytes());
            byte[] digest = md.digest();
            StringBuilder hashedFileName = new StringBuilder();
            for (byte b : digest) {
                hashedFileName.append(String.format("%02x", b));
            }

            return hashedFileName.toString() + extension;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a byte array to a java.sql.Blob object.
     * @param imageData The byte array representing the image data.
     * @return A java.sql.Blob object.
     */
    public static Blob byteArrayToBlob(byte[] imageData) throws SQLException {
        return new javax.sql.rowset.serial.SerialBlob(imageData);
    }
}
