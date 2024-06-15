package com.artsolo.bookswap.wishlist;

import com.artsolo.bookswap.book.dto.BookResponse;
import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final BookService bookService;

    @PostMapping("/add-book/{id}")
    public ResponseEntity<MessageResponse> addBookToWishlist(@PathVariable Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        wishlistService.addBookToWishlist(bookService.getBookById(id), user);
        return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added to wishlist successfully").build());
    }

    @DeleteMapping("/remove-book/{id}")
    public ResponseEntity<?> removeBookFromWishlist(@PathVariable Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (wishlistService.removeBookFromWishlist(bookService.getBookById(id), user)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book removed from wishlist successfully")
                    .build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Book still in the wishlist")).build());
    }

    @GetMapping("/get-books")
    public ResponseEntity<SuccessResponse<List<BookResponse>>> getAllWishlistBooks(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(wishlistService.getAllWishlistBooks(user)));
    }
}
