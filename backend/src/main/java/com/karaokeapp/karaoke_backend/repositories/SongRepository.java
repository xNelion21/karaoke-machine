package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Ta metoda jest używana w Twoim SongService.java do walidacji!
    boolean existsByTitle(String title);

    Optional<Song> findByYoutubeUrl(String youtubeUrl);

    // szukanie po tytule (częściowo case-insensitive)
    List<Song> findByTitleContainingIgnoreCase(String title);

    // szukanie po wykonawcy
    List<Song> findByTitleContainingIgnoreCaseAndAuthors_NameContainingIgnoreCase(String title, String artist);

    // szukanie po id kategorii
    List<Song> findByCategories_Id(Long categoryId);
}