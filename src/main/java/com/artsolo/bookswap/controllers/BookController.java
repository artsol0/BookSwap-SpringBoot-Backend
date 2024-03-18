package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewBook(@RequestBody BookRequest request, Principal currentUser) {
        if (bookService.addNewBook(request, currentUser)) {
            return ResponseEntity.ok("Book added");
        }
        return ResponseEntity.badRequest().body("Something wrong");
    }
}
