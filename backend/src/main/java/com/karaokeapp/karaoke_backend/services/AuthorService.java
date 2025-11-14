package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.karaokeapp.karaoke_backend.dto.AuthorDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapperService authorMapper;

    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono autora o ID: " + id));
        return authorMapper.toDTO(author);
    }

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        if (authorRepository.existsByNameIgnoreCase(authorDTO.getName())) {
            throw new RuntimeException("Podany autor już istnieje.");
        }
        Author authorToSave = authorMapper.toEntity(authorDTO);
        Author savedAuthor = authorRepository.save(authorToSave);

        return authorMapper.toDTO(savedAuthor);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono autora o ID: " + id);
        }

        authorRepository.deleteById(id);
    }

    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono autora o ID: " + id));

        Author authorToUpdate = authorMapper.updateEntityFromDTO(existingAuthor, authorDTO);
        Author updatedAuthor = authorRepository.save(authorToUpdate);
        return authorMapper.toDTO(updatedAuthor);
    }
}
