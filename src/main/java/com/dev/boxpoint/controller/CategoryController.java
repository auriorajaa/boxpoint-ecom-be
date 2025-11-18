package com.dev.boxpoint.controller;

import com.dev.boxpoint.model.Category;
import com.dev.boxpoint.response.ApiResponse;
import com.dev.boxpoint.service.category.ICategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        // Get all categories and add into a list
        List<Category> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(new ApiResponse("FOUND", categories));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        // Add category
        Category theCategory = categoryService.addCategory(category);

        return ResponseEntity.ok(new ApiResponse("Success", theCategory));
    }

    @GetMapping("/category/{id}/category")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        // Get category by id
        Category theCategory = categoryService.findCategoryById(id);

        return ResponseEntity.ok(new ApiResponse("Success", theCategory));
    }
}
