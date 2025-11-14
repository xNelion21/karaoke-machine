package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.dto.CategoryDTO;
import com.karaokeapp.karaoke_backend.models.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapperService {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

    public Category updateEntityFromDTO(Category category, CategoryDTO dto) {
        if (category == null || dto == null) {
            return null;
        }
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }

        return category;
    }
}
