package com.dev.boxpoint.service.image;

import com.dev.boxpoint.dtos.ImageDto;
import com.dev.boxpoint.model.Image;
import com.dev.boxpoint.model.Product;
import com.dev.boxpoint.repository.ImageRepository;
import com.dev.boxpoint.service.product.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final ImageRepository imageRepository;
    private final IProductService productService;

    @Override
    public Image getImageById(Long imageId) {
        // Find image by id
        return imageRepository.findById(imageId)
                // Throw exception if image not exist
                .orElseThrow(() -> new EntityNotFoundException("Image not found!"));
    }

    @Override
    public void deleteImageById(Long imageId) {
        // Delete image by id
        imageRepository.findById(imageId).ifPresentOrElse(imageRepository::delete, () -> {
            // Throw exception if image not exist
            throw new EntityNotFoundException("Image not found!");
        });
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        // Get image by id
        Image image = getImageById(imageId);

        try {
            // Try to update existing image
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));

            // Save new image
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            // Throw error if something goes wrong while updating image
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        // Get the product by its ID (this links the uploaded images to the correct product)
        Product product = productService.getProductById(productId);

        // Create a list to store information about all saved images
        List<ImageDto> savedImages = new ArrayList<>();

        // Loop through each uploaded file
        for (MultipartFile file : files) {
            try {
                // Create a new Image entity (this represents a row in the database)
                Image image = new Image();

                // Set the original filename of the uploaded file
                image.setFileName(file.getOriginalFilename());

                // Set the file's MIME type (like image/png or image/jpeg)
                image.setFileType(file.getContentType());

                // Convert the file's byte data into a database-friendly format (BLOB)
                image.setImage(new SerialBlob(file.getBytes()));

                // Link the image to the specific product
                image.setProduct(product);

                // Base URL for downloading images later
                String buildDownloadUrl = "/api/v1/images/image/download/";

                // Temporarily build the download URL (image ID is not known yet because it's not saved)
                String downloadUrl = buildDownloadUrl + image.getId();

                // Set this temporary URL (will be updated after saving)
                image.setDownloadUrl(downloadUrl);

                // Save the image to the database for the first time (this generates an ID)
                Image savedImage = imageRepository.save(image);

                // Now that the image has an ID, rebuild the correct download URL
                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());

                // Save again to update the record with the correct download URL
                imageRepository.save(savedImage);

                // Create a Data Transfer Object (DTO) to send back to the client
                ImageDto imageDto = new ImageDto();

                // Copy the saved image's ID, filename and download URL
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());

                // Add this DTO to the list of saved images
                savedImages.add(imageDto);
            } catch (IOException | SQLException e) {
                // If something goes wrong (like reading bytes or creating a blob), throw a runtime error
                throw new RuntimeException(e.getMessage());
            }
        }

        // Return all successfully saved image details
        return savedImages;
    }

}
