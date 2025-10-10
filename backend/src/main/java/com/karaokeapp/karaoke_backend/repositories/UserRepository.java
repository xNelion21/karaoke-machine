package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Niestandardowa metoda do wyszukiwania użytkownika po nazwie (wymagana przez Spring Security)
    Optional<User> findByUsername(String username);

    // Metoda do sprawdzania, czy nazwa użytkownika istnieje podczas rejestracji
    Boolean existsByUsername(String username);

    // Metoda do sprawdzania, czy email istnieje podczas rejestracji
    Boolean existsByEmail(String email);
}