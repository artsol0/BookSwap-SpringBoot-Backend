package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.review.ReviewRequest;
import com.artsolo.bookswap.controllers.review.ReviewResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

    public boolean reviewIsExist(Long userId, Long bookId) {
        CompositeKey compositeKey = new CompositeKey(userId, bookId);
        return reviewRepository.existsById(compositeKey);
    }

    public boolean userIsReviewWriter(User user, Review review) {
        return review.getUser().getId().equals(user.getId());
    }
}
