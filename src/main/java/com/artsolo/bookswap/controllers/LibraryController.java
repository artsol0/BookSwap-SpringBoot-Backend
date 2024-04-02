package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Library;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.LibraryService;
import com.artsolo.bookswap.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class LibraryController {
    private final LibraryService libraryService;
    private final UserService userService;
    private final BookService bookService;

    public LibraryController(LibraryService libraryService, UserService userService, BookService bookService) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.bookService = bookService;
    }

    @PostMapping("/add-book")
    public ResponseEntity<?> addBookToLibrary(@RequestBody Map<String, String> request) {
        if (request.get("userId") != null && request.get("bookId") != null) {
            User user = userService.getUserById(Long.parseLong(request.get("userId")));
            Book book = bookService.getBookById(Long.parseLong(request.get("bookId")));
            if (libraryService.addNewBookToUserLibrary(user, book)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added to library successfully")
                        .build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Failed to add book")).build());
            }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @DeleteMapping("/remove-book")
    public ResponseEntity<?> removeBookFromLibrary(@RequestBody Map<String, String> request) {
        if (request.get("userId") != null && request.get("bookId") != null) {
            User user = userService.getUserById(Long.parseLong(request.get("userId")));
            Book book = bookService.getBookById(Long.parseLong(request.get("bookId")));
            Library library = libraryService.getLibraryById(new CompositeKey(user.getId(), book.getId()));
            if (libraryService.removeBookFromUserLibrary(library)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book removed from library successfully")
                        .build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Book still in the library")).build());
            }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @GetMapping("/get-books")
    public ResponseEntity<?> getAllLibraryBooks(Principal currentUser) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(libraryService.getAllLibraryBooks(currentUser)).build());
    }
}
