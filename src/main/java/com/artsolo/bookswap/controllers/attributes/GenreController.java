package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
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
    public ResponseEntity<?> addNewGenre(@RequestBody Map<String, String> request) {
        try {
            if (request.get("genre") != null) {
                if (genreService.addNewGenre(request.get("genre"))) {
                    return ResponseEntity.ok().body(new MessageResponse("Genre was added successfully"));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        "Failed to add genre")
                ));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGenreById(@PathVariable Long id) {
        try {
            if (genreService.deleteGenreById(id)) {
                return ResponseEntity.ok().body(new MessageResponse("Genre was deleted successfully"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Genre still exits")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        try {
            Genre genre = genreService.getGenreById(id);
            if (genre != null) {
                return ResponseEntity.ok().body(new SuccessResponse<>(genre));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(new ErrorDescription(
                            HttpStatus.NOT_FOUND.value(),
                    "Genre with id '" + id + "' not found")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllGenres() {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(genreService.getAllGenres()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }
}
