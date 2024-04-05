package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.controllers.book.ReviewRequest;
import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final BookService bookService;

    public ReviewController(ReviewService reviewService, BookService bookService) {
        this.reviewService = reviewService;
        this.bookService = bookService;
    }

    @PostMapping("/{bookId}/add-review")
    public ResponseEntity<?> addBookReview(@PathVariable Long bookId, @RequestBody ReviewRequest request, Principal currentUser) {
        if (request.getReview() != null && request.getRating() != null) {
            if (reviewService.addBookRevive(bookService.getBookById(bookId), request, currentUser)){
                return ResponseEntity.ok().body(new MessageResponse("Review was added successfully"));
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Failed to add review")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @GetMapping("/{bookId}/get-reviews")
    public ResponseEntity<?> getAllBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(reviewService.getAllBookReviews(bookService.getBookById(bookId))).build());
    }

    @GetMapping("/get/{userId}/{bookId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long userId, @PathVariable Long bookId) {
        Review review = reviewService.getReviewById(userId, bookId);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(reviewService.getReviewResponse(review)).build());
    }

    @PutMapping("/update/{userId}/{bookId}")
    public ResponseEntity<?> updateReviewById(@PathVariable Long userId, @PathVariable Long bookId,
                                              @RequestBody ReviewRequest request) {
        if (request.getReview() != null && request.getRating() != null) {
            reviewService.updateReview(
                    reviewService.getReviewById(userId, bookId),
                    request.getRating(),
                    request.getReview()
            );
            return ResponseEntity.ok().body(MessageResponse.builder().message("Review was updated").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @DeleteMapping("/delete/{userId}/{bookId}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long userId, @PathVariable Long bookId) {
        Review review = reviewService.getReviewById(userId, bookId);
        if (reviewService.deleteRevive(review)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Review was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Review still in the library")).build());
    }
}
