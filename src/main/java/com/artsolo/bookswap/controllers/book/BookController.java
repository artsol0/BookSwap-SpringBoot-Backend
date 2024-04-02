package com.artsolo.bookswap.controllers.book;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;

    public BookController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewBook(@ModelAttribute AddBookRequest request, Principal currentUser) throws IOException {
        if (bookService.bookRequestIsValid(request)) {
            if (bookService.addNewBook(request, currentUser)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Filed to add new book")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id) {
        if (bookService.deleteBook(bookService.getBookById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Book still exist")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBookResponse(bookService.getBookById(id));
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookResponse).build());
    }

    @GetMapping("/get/by/genre/{id}")
    public ResponseEntity<?> getBooksByGenreId(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByGenreId(id)).build());
    }

    @GetMapping("/get/by/language/{id}")
    public ResponseEntity<?> getBooksByLanguageId(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByLanguageId(id)).build());
    }

    @GetMapping("/get/by/keyword")
    public ResponseEntity<?> getBooksByTitleOrAuthor(@RequestParam("word") String keyword) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByTitleOrAuthor(keyword)).build());
    }

    @GetMapping("/get/by/genre/and/language")
    public ResponseEntity<?> getBooksByGenreAndLanguage(@RequestBody Map<String, String> request) {
        if (request.get("genreId") != null && request.get("languageId") != null) {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(bookService.getBooksByGenreIdAndLanguageId(
                            Long.parseLong(request.get("genreId")),
                            Long.parseLong(request.get("languageId"))
                    )).build());
            }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getBookPhoto(@RequestParam("id") Long id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bookService.getBookPhoto(bookService.getBookById(id)));
    }

    @PostMapping("/{id}/add-review")
    public ResponseEntity<?> addBookReview(@PathVariable Long id, @RequestBody ReviewRequest request, Principal currentUser) {
        if (request.getReview() != null && request.getRating() != null) {
            if (reviewService.addBookRevive(bookService.getBookById(id), request, currentUser)){
                return ResponseEntity.ok().body(new MessageResponse("Review was added successfully"));
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Failed to add review")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @GetMapping("/{id}/get-reviews")
    public ResponseEntity<?> getAllBookReviews(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(reviewService.getAllBookReviews(bookService.getBookById(id))).build());
    }

}
