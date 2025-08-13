package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import com.example.demo.mapping.CategoryMapping;
import com.example.demo.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor()
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private CategoryRepository categoryRepository;
    private CategoryMapping categoryMapping;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapping::toCategoryDto)
                .toList();
    }
}
