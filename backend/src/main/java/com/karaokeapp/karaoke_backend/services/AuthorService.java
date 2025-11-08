package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Author;
import com.karaokeapp.karaoke_backend.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono autora o ID: " + id));
    }

    public Author createAuthor(Author author) {
        if (authorRepository.existsByNameIgnoreCase(author.getName())) {
            throw new RuntimeException("Podany autor już istnieje.");
        }

        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie można usunąć: nie znaleziono autora o ID: " + id);
        }

        authorRepository.deleteById(id);
    }

    public Author updateAuthor(Long id, Author author) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono autora o ID: " + id);
        }
        return authorRepository.save(author);
    }
}
