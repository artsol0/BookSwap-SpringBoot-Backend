package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final BookService bookService;

    public WishlistController(WishlistService wishlistService, BookService bookService) {
        this.wishlistService = wishlistService;
        this.bookService = bookService;
    }

    @PostMapping("/add-book/{id}")
    public ResponseEntity<?> addBookToWishlist(@PathVariable Long id, Principal currentUser) {
        if (wishlistService.addBookToWishlist(bookService.getBookById(id), currentUser)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added to wishlist successfully")
                    .build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Failed to add book")).build());
    }

    @DeleteMapping("/remove-book/{id}")
    public ResponseEntity<?> removeBookFromWishlist(@PathVariable Long id, Principal currentUser) {
        if (wishlistService.removeBookFromWishlist(bookService.getBookById(id), currentUser)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book removed from wishlist successfully")
                    .build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Book still in the wishlist")).build());
    }

    @GetMapping("/get-books")
    public ResponseEntity<?> getAllWishlistBooks(Principal currentUser) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(wishlistService.getAllWishlistBooks(currentUser)).build());
    }
}
