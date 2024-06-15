package com.artsolo.bookswap.review;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.compositekey.CompositeKey;
import com.artsolo.bookswap.review.dto.ReviewRequest;
import com.artsolo.bookswap.review.dto.ReviewResponse;
import com.artsolo.bookswap.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {this.reviewRepository = reviewRepository;}

    public Review getReviewById(Long userId, Long bookId) {
        CompositeKey compositeKey = new CompositeKey(userId, bookId);
        return reviewRepository.findById(compositeKey)
                .orElseThrow(() -> new NoDataFoundException("Review", userId, bookId));
    }

    public List<ReviewResponse> getAllBookReviews(Book book) {
        return book.getReviews().stream().map(this::getReviewResponse).collect(Collectors.toList());
    }

    public Page<ReviewResponse> getAllBookReviewsPaged(Long bookId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable);
        return reviewPage.map(this::getReviewResponse);
    }

    public ReviewResponse getReviewResponse(Review review) {
        return ReviewResponse.builder()
                .userId(review.getReviewId().getUser_id())
                .bookId(review.getReviewId().getBook_id())
                .nickname(review.getUser().getNickname())
                .userPhoto(review.getUser().getPhoto())
                .rating(review.getRating())
                .review(review.getReview())
                .build();
    }

    public ReviewResponse addBookRevive(Book book, ReviewRequest reviewRequest, User user) {
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        Review review = Review.builder()
                .reviewId(compositeKey)
                .user(user)
                .book(book)
                .rating(reviewRequest.getRating())
                .review(reviewRequest.getReview())
                .build();

        return getReviewResponse(reviewRepository.save(review));
    }

    public void updateReview(Review review, Integer rating, String reviewText) {
        review.setRating(rating);
        review.setReview(reviewText);
        reviewRepository.save(review);
    }

    public boolean deleteRevive(Review review) {
        reviewRepository.deleteById(review.getReviewId());
        return !reviewRepository.existsById(review.getReviewId());
    }

    public boolean isReviewExist(Long userId, Long bookId) {
        CompositeKey compositeKey = new CompositeKey(userId, bookId);
        return reviewRepository.existsById(compositeKey);
    }

    public boolean isUserReviewWriter(User user, Review review) {
        return review.getUser().getId().equals(user.getId());
    }
}
