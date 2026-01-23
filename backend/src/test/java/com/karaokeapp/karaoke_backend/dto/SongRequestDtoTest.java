package com.karaokeapp.karaoke_backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        dto.setCategories(Set.of("Rock"));
        dto.setLyrics("La la la");
        dto.setYoutubeUrl("https://youtube.com/xyz");

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Walidacja powinna przejść pomyślnie dla poprawnych danych");
    }

    @Test
    void shouldFailValidation_WhenTitleIsBlank() {

        SongRequestDTO dto = new SongRequestDTO();
        dto.setTitle("");
        dto.setCategories(Set.of("Pop"));

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Walidacja powinna wykryć błąd");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")),
                "Błąd powinien dotyczyć pola 'title'");
    }

    @Test
    void shouldFailValidation_WhenTitleIsTooLong() {
        SongRequestDTO dto = new SongRequestDTO();
        String longTitle = "A".repeat(101);
        dto.setTitle(longTitle);
        dto.setCategories(Set.of("Metal"));

        Set<ConstraintViolation<SongRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());

        boolean hasCorrectMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Tytuł piosenki jest za długi"));

        assertTrue(hasCorrectMessage, "Powinien wystąpić błąd o zbyt długim tytule");
    }
}