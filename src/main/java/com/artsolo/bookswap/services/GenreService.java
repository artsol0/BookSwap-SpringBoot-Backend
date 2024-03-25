package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Genre;
import com.artsolo.bookswap.repositoryes.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public boolean addNewGenre(String genreName) {
        Genre newGenre = new Genre();
        newGenre.setGenre(genreName);
        newGenre = genreRepository.save(newGenre);
        return genreRepository.existsById(newGenre.getId());
    }

    public boolean deleteGenreById(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            genreRepository.deleteById(genre.get().getId());
            return !genreRepository.existsById(genre.get().getId());
        }
        return false;
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    public List<Genre> getAllGenres() {return genreRepository.findAll();}
}
