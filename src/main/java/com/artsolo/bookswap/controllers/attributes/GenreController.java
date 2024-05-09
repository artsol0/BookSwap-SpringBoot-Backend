package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewGenre(@RequestParam("genre") String genre) {
        if (genreService.addNewGenre(genre)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Genre was added successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Filed to add new genre")).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGenreById(@PathVariable Long id) {
        if (genreService.deleteGenre(genreService.getGenreById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Genre was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Genre still exist")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(genreService.getGenreById(id)).build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllGenres() {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(genreService.getAllGenres()).build());
    }
}
