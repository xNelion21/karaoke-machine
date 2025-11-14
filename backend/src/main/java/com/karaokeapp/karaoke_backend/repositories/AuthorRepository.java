package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByNameIgnoreCase(String name);
}
