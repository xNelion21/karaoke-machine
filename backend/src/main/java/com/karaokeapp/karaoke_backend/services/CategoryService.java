package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Category;
import com.karaokeapp.karaoke_backend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono kategorii o ID: " + id));
    }

    public Category createCategory(Category category){
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new RuntimeException("Kategoria o nazwie '" + category.getName() + "' już istnieje.");
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono kategorii o ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public Category updateCategory(Long id, Category category) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono kategorii o ID: " + id);
        }
        return categoryRepository.save(category);
    }
}
