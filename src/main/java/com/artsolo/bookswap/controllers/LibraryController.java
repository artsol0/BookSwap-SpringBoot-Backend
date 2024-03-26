package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class LibraryController {
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/add-book")
    public ResponseEntity<String> addBookToLibrary(@RequestBody Map<String, String> request) {
        try {
            if(libraryService.addNewBookToUserLibrary(request)) {
                return ResponseEntity.ok("success");
            }
            return ResponseEntity.ok("failed");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove-book")
    public ResponseEntity<String> removeBookFromLibrary(@RequestBody Map<String, String> request) {
        try {
            if(libraryService.removeBookFromUserLibrary(request)) {
                return ResponseEntity.ok("success");
            }
            return ResponseEntity.ok("failed");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-books")
    public ResponseEntity<?> getAllWishlistBooks(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(libraryService.getAllLibraryBooks(currentUser));
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
