package com.dev.boxpoint.service.product;

import com.dev.boxpoint.dtos.ProductDto;
import com.dev.boxpoint.model.Product;
import com.dev.boxpoint.request.AddProductRequest;
import com.dev.boxpoint.request.UpdateProductRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product updateProduct(UpdateProductRequest product, Long productId);
    Product getProductById(Long productId);
    void deleteProductById(Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrandAndName(String brand, String name);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByName(String name);
    List<ProductDto> getConvertedProducts(List<Product> products);
    ProductDto convertToDto(Product product);
}
