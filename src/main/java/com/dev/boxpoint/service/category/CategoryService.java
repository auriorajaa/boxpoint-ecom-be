package com.dev.boxpoint.service.category;

import com.dev.boxpoint.model.Category;
import com.dev.boxpoint.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                // Check if category is not already exist
                .filter(c -> !categoryRepository.existsByName(c.getName()))
                // Save the category if it's not already exist
                .map(categoryRepository::save)
                // If the category already exist, throw exception
                .orElseThrow(() -> new EntityExistsException(category.getName() + " already exists!"));
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        // Make sure the updated category is existed
        return Optional.ofNullable(findCategoryById(categoryId))
                .map(oldCategory -> {
                    // Change old category name to new one
                    oldCategory.setName(category.getName());

                    return categoryRepository.save(oldCategory);
                })
                // If category not exist, throw exception
                .orElseThrow(() -> new EntityNotFoundException("Category not found!"));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // Find the category by id
        categoryRepository.findById(categoryId)
                // Delete the category if it's existed
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    // If category not exist, throw exception
                    throw new EntityNotFoundException("Category not found");
                });
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        // Find the category by id
        return categoryRepository.findById(categoryId)
                // Throw exception if category not exist
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
