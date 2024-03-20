package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<String> addBookToWishlist(@PathVariable Long id, Principal currentUser) {
        try {
            if (wishlistService.addBookToWishlist(id, currentUser)) {
                return ResponseEntity.ok("Book added to wishlist");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeBookFromWishlist(@PathVariable Long id, Principal currentUser) {
        try {
            if (wishlistService.removeBookFromWishlist(id, currentUser)) {
                return ResponseEntity.ok("Book removed from wishlist");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
