package com.artsolo.bookswap.controllers.book;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?> addNewBook(@ModelAttribute AddBookRequest request, Principal currentUser) {
        try {
            if (bookService.bookRequestIsValid(request)) {
                if (bookService.addNewBook(request, currentUser)) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Filed to add new book")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                if (bookService.deleteBook(book.get())) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Book was deleted successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Book still exist")).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Book with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                BookResponse bookResponse = bookService.getBookResponse(book.get());
                return ResponseEntity.ok().body(SuccessResponse.builder().data(bookResponse).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(
                    new ErrorDescription(HttpStatus.NOT_FOUND.value(), "Book with id '" + id + "' not found")).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/by/genre/{id}")
    public ResponseEntity<?> getBooksByGenreId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByGenreId(id)).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/by/language/{id}")
    public ResponseEntity<?> getBooksByLanguageId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByLanguageId(id)).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/by/keyword")
    public ResponseEntity<?> getBooksByTitleOrAuthor(@RequestParam("word") String keyword) {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByTitleOrAuthor(keyword)).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/by/genre/and/language")
    public ResponseEntity<?> getBooksByGenreAndLanguage(@RequestBody Map<String, String> request) {
        try {
            if (request.get("genreId") != null && request.get("languageId") != null) {
                return ResponseEntity.ok().body(SuccessResponse.builder()
                                .data(bookService.getBooksByGenreIdAndLanguageId(
                                Long.parseLong(request.get("genreId")),
                                Long.parseLong(request.get("languageId"))
                                )).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getBookPhoto(@RequestParam("id") Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bookService.getBookPhoto(book.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Book with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @PostMapping("/{id}/add-review")
    public ResponseEntity<?> addBookReview(@PathVariable Long id, @RequestBody ReviewRequest request, Principal currentUser) {
        try {
            if (request.getReview() != null && request.getRating() != null) {
                Optional<Book> book = bookService.getBookById(id);
                if (book.isPresent()) {
                    if (reviewService.addBookRevive(book.get(), request, currentUser)){
                        return ResponseEntity.ok().body(new MessageResponse("Review was added successfully"));
                    }
                    return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                            HttpStatus.BAD_REQUEST.value(), "Failed to add review")).build());
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.NOT_FOUND.value(), "Book with id '" + id + "' not found")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/{id}/get-reviews")
    public ResponseEntity<?> getAllBookReviews(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok().body(SuccessResponse.builder()
                        .data(reviewService.getAllBookReviews(book.get())).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(),
                    "Book with id '" + id + "' not found")).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

}
