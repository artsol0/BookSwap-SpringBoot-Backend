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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final BookService bookService;

    @PostMapping("/{bookId}/add-review")
    public ResponseEntity<?> addBookReview(@PathVariable Long bookId, @RequestBody @Valid ReviewRequest request,
                                           Principal currentUser)
    {
        if (reviewService.addBookRevive(bookService.getBookById(bookId), request, currentUser)){
            return ResponseEntity.ok().body(new MessageResponse("Review was added successfully"));
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Failed to add review")).build());
    }

    @GetMapping("/{bookId}/get-reviews")
    public ResponseEntity<SuccessResponse<List<ReviewResponse>>> getAllBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok().body(new SuccessResponse<>(reviewService.getAllBookReviews(bookService.getBookById(bookId))));
    }

    @GetMapping("/{bookId}/get-reviews-page")
    public ResponseEntity<SuccessResponse<Page<ReviewResponse>>> getAllBookReviewsPage(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, 5);
        Page<ReviewResponse> reviewResponsePage = reviewService.getAllBookReviewsPaged(bookId, pageable);
        return ResponseEntity.ok().body(new SuccessResponse<>(reviewResponsePage));
    }

    @GetMapping("/get/{userId}/{bookId}")
    public ResponseEntity<SuccessResponse<ReviewResponse>> getReviewById(@PathVariable Long userId, @PathVariable Long bookId) {
        Review review = reviewService.getReviewById(userId, bookId);
        return ResponseEntity.ok().body(new SuccessResponse<>(reviewService.getReviewResponse(review)));
    }

    @GetMapping("/exist/{userId}/{bookId}")
    public ResponseEntity<SuccessResponse<Boolean>> getReviewExistence(@PathVariable Long userId, @PathVariable Long bookId) {
        return ResponseEntity.ok().body(new SuccessResponse<>(reviewService.reviewIsExist(userId, bookId)));
    }

    @PutMapping("/update/{userId}/{bookId}")
    public ResponseEntity<?> updateReviewById(@PathVariable Long userId, @PathVariable Long bookId,
                                              @RequestBody @Valid ReviewRequest request, Principal currentUser) {
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
