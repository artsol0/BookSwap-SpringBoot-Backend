package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.ReviewRequest;
import com.artsolo.bookswap.controllers.book.ReviewResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.ReviewRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getReviewById(Long userId, Long bookId) {
        CompositeKey compositeKey = new CompositeKey(userId, bookId);
        return reviewRepository.findById(compositeKey)
                .orElseThrow(() -> new NoDataFoundException("Review", userId, bookId));
    }

    public ReviewResponse getReviewResponse(Review review) {
        return ReviewResponse.builder()
                .userId(review.getReviewId().getUser_id())
                .bookId(review.getReviewId().getBook_id())
                .nickname(review.getUser().getNickname())
                .rating(review.getRating())
                .review(review.getReview())
                .build();
    }

    public boolean addBookRevive(Book book, ReviewRequest reviewRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        Review review = Review.builder()
                .reviewId(compositeKey)
                .user(user)
                .book(book)
                .rating(reviewRequest.getRating())
                .review(reviewRequest.getReview())
                .build();

        review = reviewRepository.save(review);
        return reviewRepository.existsById(review.getReviewId());
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

    public List<ReviewResponse> getAllBookReviews(Book book) {
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = book.getReviews();
        for (Review review : reviews) {
            responses.add(ReviewResponse.builder()
                    .userId(review.getReviewId().getUser_id())
                    .bookId(review.getReviewId().getBook_id())
                    .nickname(review.getUser().getNickname())
                    .rating(review.getRating())
                    .review(review.getReview())
                    .build());
        }
        return responses;
    }

    public boolean userIsReviewWriter(User user, Review review) {
        return review.getUser().getId().equals(user.getId());
    }
}
