package com.dev.boxpoint.service.product;

import com.dev.boxpoint.dtos.ImageDto;
import com.dev.boxpoint.dtos.ProductDto;
import com.dev.boxpoint.model.*;
import com.dev.boxpoint.repository.*;
import com.dev.boxpoint.request.AddProductRequest;
import com.dev.boxpoint.request.UpdateProductRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {

        // Check if product is already exist
        if (productExists(request.getName(), request.getBrand())) {
            throw new EntityExistsException(request.getName() + " already exists!");
        }

        // Set existing category to the product
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {

                    // Create new category related to the product if it's not exists
                    Category newCategory = new Category(request.getCategory().getName());

                    // Save new category
                    return categoryRepository.save(newCategory);
                });

        // Set category as product category
        request.setCategory(category);

        // Save product
        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand) {
        // Find product that exist with given name and brand
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        // Return value of product (name, brand, etc.)
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        // Find the existing product that want to be updated with given id
        return productRepository.findById(productId)
                // Mapping new information to existing product
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                // Save the updated product
                .map(productRepository::save)
                // Throw error if product not found
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        // Change value of existing product with given new information
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        // Find category by name from request
        Category category = categoryRepository.findByName(request.getCategory().getName());
        // Set category to existing product
        existingProduct.setCategory(category);

        // Return
        return existingProduct;
    }

    @Override
    public void deleteProductById(Long productId) {
        // Try to find the product in the database by its ID
        productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    // If the product exists, do the following:
                    // Find all cart items that include this product
                    List<CartItem> cartItems = cartItemRepository.findByProductId(productId);

                    // Loop through each cart item related to this product
                    cartItems.forEach(cartItem -> {
                        // Get the cart that owns this cart item
                        Cart cart = cartItem.getCart();

                        // Remove the cart item from the cart’s item list (to keep data consistent)
                        cart.removeItem(cartItem);

                        // Delete this cart item from the database
                        cartItemRepository.delete(cartItem);
                    });

                    // Find all order items that include this product
                    List<OrderItem> orderItems = orderItemRepository.findByProductId(productId);

                    // Loop through each order item related to this product
                    orderItems.forEach(orderItem -> {
                        // Break the connection between the order item and the product
                        // (so deleting the product won't cause a foreign key constraint error)
                        orderItem.setProduct(null);

                        // Save the updated order item
                        orderItemRepository.save(orderItem);
                    });

                    // Handle the relationship between the product and its category
                    Optional.ofNullable(product.getCategory())
                            // If the product has a category, remove it from that category’s product list
                            .ifPresent(category -> category.getProducts().remove(product));

                    // Remove the category link from the product itself
                    product.setCategory(null);

                    // Finally, delete the product from the database
                    productRepository.deleteById(product.getId());
                }, () -> {
                    // If the product does NOT exist, throw an exception
                    throw new EntityNotFoundException("Product not found!");
                });
    }

    @Override
    public Product getProductById(Long productId) {
        // Find product by id
        return productRepository.findById(productId)
                // Throw error if not found
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByNameAndBrand(brand, name);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        // Convert a list of Product entities into a list of ProductDto objects
        // 'products.stream()' turns the list into a stream (to process each item easily)
        // 'map(this::convertToDto)' calls convertToDto() for each product
        // '.toList()' collects the converted DTOs back into a list
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        // Use ModelMapper to automatically copy matching fields
        // from the Product entity to a new ProductDto
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        // Fetch all images linked to this product from the database
        List<Image> images = imageRepository.findByProductId(product.getId());

        // Convert each Image entity into an ImageDto using ModelMapper
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();

        // Attach the converted image list to the ProductDto
        productDto.setImages(imageDtos);

        // Return the completed ProductDto (with product data + image data)
        return productDto;
    }

}
