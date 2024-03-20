package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService, LibraryService libraryService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewBook(@RequestBody BookRequest request, Principal currentUser) {
        try {
            if (bookService.addNewBook(request, currentUser)) {
                return ResponseEntity.ok("Book added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
