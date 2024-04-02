package com.artsolo.bookswap.services;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.Genre;
import com.artsolo.bookswap.repositoryes.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Genre", id));
    }

    public List<Genre> getAllGenres() {return genreRepository.findAll();}
}
