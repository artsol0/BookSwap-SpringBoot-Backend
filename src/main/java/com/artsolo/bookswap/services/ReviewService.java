package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.ReviewRequest;
import com.artsolo.bookswap.controllers.book.ReviewResponse;
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

    public boolean deleteReviveById(Long bookId, Long userId) {
        Optional<Review> revive = reviewRepository.findById(new CompositeKey(userId, bookId));
        if (revive.isPresent()) {
            reviewRepository.deleteById(revive.get().getReviewId());
            return reviewRepository.existsById(revive.get().getReviewId());
        }
        return false;
    }

    public List<ReviewResponse> getAllBookReviews(Book book) {
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = book.getReviews();
        for (Review review : reviews) {
            responses.add(ReviewResponse.builder()
                    .nickname(review.getUser().getNickname())
                    .rating(review.getRating())
                    .review(review.getReview())
                    .build());
        }
        return responses;
    }
}
