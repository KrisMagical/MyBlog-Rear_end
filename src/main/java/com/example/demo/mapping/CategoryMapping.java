package com.example.demo.mapping;

import com.example.demo.dto.CategoryDto;
import com.example.demo.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapping {
    CategoryDto toCategoryDto(Category category);

    Category toCategoryEntity(CategoryDto dto);
}
