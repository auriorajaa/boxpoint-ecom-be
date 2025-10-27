package com.dev.boxpoint.dtos;

import com.dev.boxpoint.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int Inventory;
    private String description;
    private Category category;
    private List<ImageDto> images;
}
