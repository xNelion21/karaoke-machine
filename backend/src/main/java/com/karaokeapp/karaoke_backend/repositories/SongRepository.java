package com.karaokeapp.karaoke_backend.repositories;

import com.karaokeapp.karaoke_backend.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Ta metoda jest używana w Twoim SongService.java do walidacji!
    boolean existsByTitle(String title);
}