package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Ta metoda jest używana w Twoim SongService.java do walidacji!
    boolean existsByTitle(String title);

    // szukanie po tytule (częściowo case-insensitive)
    List<Song> findByTitleContainingIgnoreCase(String title);

    // szukanie po wykonawcy
    List<Song> findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCase(String title, String artist);

    // szukanie po gatunku
    List<Song> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(String title, String genre);

    // szukanie po tytule lub autorze
    List<Song> findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCaseAndGenreIgnoreCase(String title, String artist, String genre);

    // szukanie po id kategorii
    List<Song> findByCategories_Id(Long categoryId);
}