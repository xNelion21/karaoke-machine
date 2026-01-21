package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SongRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsAreCorrect() {
        SongRequestDTO dto = new SongRequestDTO();
        dto.setTitle("Poprawny Tytuł");
        dto.setCategory("Rock");
        dto.setLyrics("La la la");

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_WhenTitleIsBlank() {
        SongRequestDTO dto = new SongRequestDTO();
        dto.setTitle("");
        // dto.setAuthorsIds(Set.of(1L)); - cos bledy zwraca

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailValidation_WhenTitleIsTooLong() {
        SongRequestDTO dto = new SongRequestDTO();
        String longTitle = "A".repeat(101);
        dto.setTitle(longTitle);
        dto.setAuthorsIds(Set.of(1L));

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        String message = violations.iterator().next().getMessage();
        assertEquals("Tytuł piosenki jest za długi", message);
    }

    @Test
    void shouldFailValidation_WhenAuthorsListIsEmpty() {
        SongRequestDTO dto = new SongRequestDTO();
        dto.setTitle("Dobra piosenka");
        dto.setAuthorsIds(Collections.emptySet());

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Przynajmniej jeden autor")));
    }
}