package com.dev.boxpoint.controller;

import com.dev.boxpoint.dtos.ProductDto;
import com.dev.boxpoint.model.Product;
import com.dev.boxpoint.request.AddProductRequest;
import com.dev.boxpoint.request.UpdateProductRequest;
import com.dev.boxpoint.response.ApiResponse;
import com.dev.boxpoint.service.product.IProductService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        // Retrieve all products from the service layer
        List<Product> products = productService.getAllProducts();

        // Convert Product entities to ProductDto for a clean response, including related images
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        // Return 200 OK with the list of DTOs
        return ResponseEntity.ok(new ApiResponse("FOUND", convertedProducts));
    }

    @GetMapping("/product/{productId}/product")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            // Retrieve product by its ID
            Product product = productService.getProductById(productId);

            // Convert entity to DTO for the response
            ProductDto productDto = productService.convertToDto(product);

            // Return 200 OK with the found product DTO
            return ResponseEntity.ok(new ApiResponse("FOUND", productDto));
        } catch (EntityNotFoundException e) {
            // Handle case where product is not found (404 NOT FOUND)
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("Product not found with given id!", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            // Add the new product via the service layer
            Product theProduct = productService.addProduct(product);
            // Convert the result to DTO
            ProductDto productDto = productService.convertToDto(theProduct);

            // Return 200 OK on success
            return ResponseEntity.ok(new ApiResponse("Product successfully added!", productDto));
        } catch (EntityExistsException e) {
            // Handle case where product already exists (409 CONFLICT)
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestBody UpdateProductRequest request,
            @PathVariable Long productId) {
        try {
            // Update the existing product
            Product theProduct = productService.updateProduct(request, productId);
            // Convert the updated entity to DTO
            ProductDto productDto = productService.convertToDto(theProduct);

            // Return 200 OK on success
            return ResponseEntity.ok(new ApiResponse("Product successfully updated!", productDto));
        } catch (EntityNotFoundException e) {
            // Handle case where product to update is not found (404 NOT FOUND)
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            // Delete the product by its ID
            productService.deleteProductById(productId);

            // Return 200 OK on success, sending back the deleted ID
            return ResponseEntity.ok(new ApiResponse("Product successfully deleted!", productId));
        } catch (EntityNotFoundException e) {
            // Handle case where product to delete is not found (404 NOT FOUND)
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(
            @RequestParam String brandName,
            @RequestParam String productName) {
        try {
            // Search for products by both brand name and product name
            List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
            // Convert results to DTOs
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

            // Return 200 OK with the filtered list
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            // Handle general errors (500 INTERNAL SERVER ERROR)
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(
            @RequestParam String category,
            @RequestParam String brandName
    ) {
        try {
            // Search for products by both category and brand name
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brandName);
            // Convert results to DTOs
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

            // Return 200 OK with the filtered list
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            // Handle general errors (500 INTERNAL SERVER ERROR)
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/products/{name}/products")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
        // Search for products whose name contains the given string
        List<Product> products = productService.getProductsByName(name);
        // Convert results to DTOs
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        // Return 200 OK with the list of matching products
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/product/by-brand")
    public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand) {
        // Search for products by a specific brand
        List<Product> products = productService.getProductsByBrand(brand);
        // Convert results to DTOs
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        // Return 200 OK with the list of matching products
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/product/{category}/all/products")
    public ResponseEntity<ApiResponse> findProductsByCategory(@PathVariable String category) {
        // Search for all products within a specific category
        List<Product> products = productService.getProductsByCategory(category);
        // Convert results to DTOs
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        // Return 200 OK with the list of matching products
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }
}