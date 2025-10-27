package com.dev.boxpoint.controller;

import com.dev.boxpoint.model.Category;
import com.dev.boxpoint.response.ApiResponse;
import com.dev.boxpoint.service.category.ICategoryService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            // Get all categories and add into a list
            List<Category> categories = categoryService.getAllCategories();

            return ResponseEntity.ok(new ApiResponse("FOUND", categories));
        } catch (Exception e) {
            // Return internal server error if something goes wrong
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error: ", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        try {
            // Add category
            Category theCategory = categoryService.addCategory(category);

            return ResponseEntity.ok(new ApiResponse("Success", theCategory));
        } catch (EntityExistsException e) {
            // Return conflict if category already existed
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse("Error: ", e.getMessage()));
        }
    }

    @GetMapping("/category/{id}/category")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            // Get category by id
            Category theCategory = categoryService.findCategoryById(id);

            return ResponseEntity.ok(new ApiResponse("Success", theCategory));
        } catch (EntityNotFoundException e) {
            // Return not found if category with certain/given id not exist
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("Error: ", e.getMessage()));
        }
    }
}
