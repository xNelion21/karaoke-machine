package com.karaokeapp.karaoke_backend.services;
import com.karaokeapp.karaoke_backend.dto.SongRequestDTO;
import com.karaokeapp.karaoke_backend.dto.SongResponseDTO;
import com.karaokeapp.karaoke_backend.models.Song;
import com.karaokeapp.karaoke_backend.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongMapperService songMapper;

    public SongResponseDTO createSong(SongRequestDTO requestDTO) {
        if (songRepository.existsByTitle(requestDTO.getTitle())) {
            throw new RuntimeException("Piosenka o tytule '" + requestDTO.getTitle() + "' już istnieje.");
        }

        Song songToSave = songMapper.toEntity(requestDTO);
        Song savedSong = songRepository.save(songToSave);

        return songMapper.toResponseDTO(savedSong);
    }

    public List<SongResponseDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(songMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public SongResponseDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono piosenki o ID: " + id));
        return songMapper.toResponseDTO(song);
    }


    public SongResponseDTO updateSong(Long id, SongRequestDTO requestDTO) {
        Song existingSong = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono piosenki o ID: " + id));

        Song songToUpdate = songMapper.updateEntityFromDTO(existingSong, requestDTO);
        Song updatedSong = songRepository.save(songToUpdate);
        return songMapper.toResponseDTO(updatedSong);
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono piosenki o ID: " + id);
        }
        songRepository.deleteById(id);
    }

    }