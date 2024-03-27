package com.artsolo.bookswap.controllers.book;

import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.LibraryService;
import com.artsolo.bookswap.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;

    public BookController(BookService bookService, LibraryService libraryService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewBook(@ModelAttribute BookRequest request, Principal currentUser) {
        try {
            if (bookService.addNewBook(request, currentUser)) {
                return ResponseEntity.ok("Book added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable Long id) {
        try {
            if(bookService.deleteBookById(id)) {
                return ResponseEntity.ok("Book deleted");
            }
            return ResponseEntity.badRequest().body("Book still exist");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(bookService.getBookById(id));
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getBookPhoto(@RequestParam("id") Long id) {
        try {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bookService.getBookPhoto(id));
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/add-review")
    public ResponseEntity<String> addBookReview(@PathVariable Long id, @RequestBody ReviewRequest request, Principal currentUser) {
        try {
            if (reviewService.addBookRevive(id, request, currentUser)){
                return ResponseEntity.ok("Revive added");
            }
            return ResponseEntity.badRequest().body("Revive was not added");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/get-reviews")
    public ResponseEntity<?> getAllBookReviews(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(reviewService.getAllBookReviews(id));
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
