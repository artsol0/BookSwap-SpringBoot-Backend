package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Genre;
import com.artsolo.bookswap.repositoryes.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public boolean addNewGenre(String genreName) {
        Genre newGenre = Genre.builder().genre(genreName).build();
        newGenre = genreRepository.save(newGenre);
        return genreRepository.existsById(newGenre.getId());
    }

    public boolean deleteGenre(Genre genre) {
        genreRepository.deleteById(genre.getId());
        return !genreRepository.existsById(genre.getId());
    }

    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    public List<Genre> getAllGenres() {return genreRepository.findAll();}
}
