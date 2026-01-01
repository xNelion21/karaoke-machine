package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
