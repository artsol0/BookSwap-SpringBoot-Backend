package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.controllers.book.ReviewRequest;
import com.artsolo.bookswap.controllers.book.ReviewResponse;
import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.enums.Role;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    @GetMapping("/{bookId}/get-reviews-page")
    public ResponseEntity<?> getAllBookReviewsPage(@PathVariable Long bookId, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<ReviewResponse> reviewResponsePage = reviewService.getAllBookReviewsPaged(bookId, pageable);
        return ResponseEntity.ok().body(SuccessResponse.builder().data(reviewResponsePage).build());
    }

    @GetMapping("/get/{userId}/{bookId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long userId, @PathVariable Long bookId) {
        Review review = reviewService.getReviewById(userId, bookId);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(reviewService.getReviewResponse(review)).build());
    }

    @GetMapping("/exist/{userId}/{bookId}")
    public ResponseEntity<?> getReviewExistence(@PathVariable Long userId, @PathVariable Long bookId) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(reviewService.reviewIsExist(userId, bookId)).build());
    }

    @PutMapping("/update/{userId}/{bookId}")
    public ResponseEntity<?> updateReviewById(@PathVariable Long userId, @PathVariable Long bookId,
                                              @RequestBody ReviewRequest request, Principal currentUser) {
        if (request.getReview() != null && request.getRating() != null) {
            User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
            Review review = reviewService.getReviewById(userId, bookId);
            if (reviewService.userIsReviewWriter(user, review) ||
                    (user.getRole().equals(Role.ADMINISTRATOR) ||
                    user.getRole().equals(Role.MODERATOR))) {
                reviewService.updateReview(
                        review,
                        request.getRating(),
                        request.getReview()
                );
                return ResponseEntity.ok().body(MessageResponse.builder().message("Review was updated").build());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.FORBIDDEN.value(), "You are not review writer")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @DeleteMapping("/delete/{userId}/{bookId}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long userId, @PathVariable Long bookId,
                                              Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Review review = reviewService.getReviewById(userId, bookId);
        if (reviewService.userIsReviewWriter(user, review) || user.getRole().equals(Role.ADMINISTRATOR)) {
            if (reviewService.deleteRevive(review)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Review was deleted successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Review still in the library")).build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not review writer")).build());
    }
}
