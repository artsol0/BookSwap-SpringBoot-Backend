package com.artsolo.bookswap.attributes.genre;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse<Genre>> addNewGenre(@RequestParam("genre") String genre) {
        return ResponseEntity.ok().body(new SuccessResponse<>(genreService.addNewGenre(genre)));
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
    public ResponseEntity<SuccessResponse<Genre>> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new SuccessResponse<>(genreService.getGenreById(id)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<SuccessResponse<List<Genre>>> getAllGenres() {
        return ResponseEntity.ok().body(new SuccessResponse<>(genreService.getAllGenres()));
    }
}
