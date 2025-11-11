package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.karaokeapp.karaoke_backend.dto.CategoryDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapperService categoryMapper;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono kategorii o ID: " + id));
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new RuntimeException("Kategoria o nazwie '" + categoryDTO.getName() + "' już istnieje.");
        }
        Category categoryToSave = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(categoryToSave);

        return categoryMapper.toDTO(savedCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono kategorii o ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono kategorii o ID: " + id));

        Category categoryToUpdate = categoryMapper.updateEntityFromDTO(existingCategory, categoryDTO);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return categoryMapper.toDTO(updatedCategory);
    }
}
