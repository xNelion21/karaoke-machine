package com.karaokeapp.karaoke_backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategoryUsingBuilder() {
        Category category = Category.builder()
                .id(1L)
                .name("Rock")
                .build();

        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("Rock", category.getName());
    }

    @Test
    void shouldCreateCategoryUsingAllArgsConstructor() {
        Category category = new Category(10L, "Pop");

        assertEquals(10L, category.getId());
        assertEquals("Pop", category.getName());
    }

    @Test
    void shouldSetValuesUsingSetters() {
        Category category = new Category();

        category.setId(5L);
        category.setName("Jazz");

        assertEquals(5L, category.getId());
        assertEquals("Jazz", category.getName());
    }
}