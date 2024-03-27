package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
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
    public ResponseEntity<?> addBookToLibrary(@RequestBody Map<String, String> request) {
        try {
            if (request.get("userId") != null && request.get("bookId") != null) {
                if (libraryService.addNewBookToUserLibrary(request)) {
                    return ResponseEntity.ok().body(new MessageResponse("Book was added to library successfully"));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        "Failed to add book")
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

    @DeleteMapping("/remove-book")
    public ResponseEntity<?> removeBookFromLibrary(@RequestBody Map<String, String> request) {
        try {
            if (request.get("userId") != null && request.get("bookId") != null) {
                if (libraryService.removeBookFromUserLibrary(request)) {
                    return ResponseEntity.ok().body(new MessageResponse("Book was removed from library successfully"));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        "Book still in the library")
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

    @GetMapping("/get-books")
    public ResponseEntity<?> getAllLibraryBooks(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(libraryService.getAllLibraryBooks(currentUser)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }
}
