package com.dev.boxpoint.controller;

import com.dev.boxpoint.dtos.ImageDto;
import com.dev.boxpoint.model.Image;
import com.dev.boxpoint.response.ApiResponse;
import com.dev.boxpoint.service.image.IImageService;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {

    private final IImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("productId") Long productId) {
        // Call the service layer to save all uploaded images linked to the given product ID
        List<ImageDto> imageDto = imageService.saveImages(productId, files);

        return ResponseEntity.ok(new ApiResponse("Images uploaded successfully!", imageDto));
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        // Get the Image object from the database using its ID
        Image image = imageService.getImageById(imageId);

        // Convert the image BLOB (binary data stored in database) into a byte array
        ByteArrayResource resource = new ByteArrayResource(
                image.getImage().getBytes(1, (int) image.getImage().length()));

        // Build and return an HTTP response containing the image file
        return ResponseEntity.ok() // Returns a 200 OK HTTP status
                // Set the Content-Type header based on the image file type (e.g. image/jpeg)
                .contentType(MediaType.parseMediaType(image.getFileType()))
                // Set the Content-Disposition header so the browser knows this is a downloadable file
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/image/{imageId}/update")
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable Long imageId,
            @RequestBody MultipartFile file) {
        // Update image with file and image id information
        imageService.updateImage(file, imageId);

        // Return successfully message
        return ResponseEntity.ok(new ApiResponse("Image updated successfully!", null));
    }

    @DeleteMapping("/image/{imageId}/delete")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        // Delete image with given id
        imageService.deleteImageById(imageId);

        // Return success message
        return ResponseEntity.ok(new ApiResponse("Image successfully deleted!", null));
    }
}
