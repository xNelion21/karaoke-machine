package com.karaokeapp.karaoke_backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SongTest {

    @Test
    void shouldSetAndGetBasicFields() {

        Song song = new Song();
        String title = "Ostatnia nocka";
        String ytUrl = "https://youtube.com/watch?v=123";
        String thumb = "http://img.com/1.jpg";


        song.setTitle(title);
        song.setYoutubeUrl(ytUrl);
        song.setThumbnailUrl(thumb);
        song.setId(1L);

        assertEquals("Ostatnia nocka", song.getTitle());
        assertEquals("https://youtube.com/watch?v=123", song.getYoutubeUrl());
        assertEquals("http://img.com/1.jpg", song.getThumbnailUrl());
        assertEquals(1L, song.getId());
    }


    @Test
    void shouldInitializeEmptyCollections_NotReturnNull() {

        Song song = new Song();

        assertNotNull(song.getAuthors(), "Lista autorów nie może być nullem!");
        assertTrue(song.getAuthors().isEmpty(), "Na początku lista autorów powinna być pusta");

        assertNotNull(song.getCategories(), "Lista kategorii nie może być nullem!");
        assertTrue(song.getCategories().isEmpty());

        assertNotNull(song.getLyricLines(), "Lista linii tekstu nie może być nullem!");
        assertTrue(song.getLyricLines().isEmpty());
    }

    @Test
    void shouldVerifyEqualityBetweenSongs() {

        Song song1 = new Song();
        song1.setTitle("Test Song");
        song1.setYoutubeUrl("url1");

        Song song2 = new Song();
        song2.setTitle("Test Song");
        song2.setYoutubeUrl("url1");

        assertEquals(song1, song2, "Obiekty z tymi samymi danymi powinny być równe (dzięki @Data)");
        assertEquals(song1.hashCode(), song2.hashCode(), "HashCode też musi być taki sam");
    }
}