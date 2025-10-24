package com.dev.boxpoint.request;

import com.dev.boxpoint.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int Inventory;
    private String description;
    private Category category;
}
