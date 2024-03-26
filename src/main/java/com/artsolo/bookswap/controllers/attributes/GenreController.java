package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.models.Genre;
import com.artsolo.bookswap.services.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewGenre(@RequestBody Map<String, String> request) {
        try {
            if (genreService.addNewGenre(request.get("genre"))) {
                return ResponseEntity.ok("New genre added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGenreById(@PathVariable Long id) {
        try {
            if (genreService.deleteGenreById(id)) {
                return ResponseEntity.ok("Genre was deleted");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        try {
            Genre genre = genreService.getGenreById(id);
            if (genre != null) {
                return ResponseEntity.ok().body(genre);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllGenres() {
        try {
            return ResponseEntity.ok().body(genreService.getAllGenres());
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
