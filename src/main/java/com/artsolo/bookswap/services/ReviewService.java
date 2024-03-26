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
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addBookRevive(Long bookId, ReviewRequest reviewRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            CompositeKey compositeKey = new CompositeKey(user.getId(), book.get().getId());
            Review review = Review.builder()
                    .reviewId(compositeKey)
                    .user(user)
                    .book(book.get())
                    .rating(reviewRequest.getRating())
                    .review(reviewRequest.getReview())
                    .build();

            review = reviewRepository.save(review);
            return reviewRepository.existsById(review.getReviewId());
        }
        return false;
    }

    public boolean deleteReviveById(Long bookId, Long userId) {
        Optional<Review> revive = reviewRepository.findById(new CompositeKey(userId, bookId));
        if (revive.isPresent()) {
            reviewRepository.deleteById(revive.get().getReviewId());
            return reviewRepository.existsById(revive.get().getReviewId());
        }
        return false;
    }

    public List<ReviewResponse> getAllBookReviews(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        List<ReviewResponse> responses = new ArrayList<>();
        if (book.isPresent()) {
            List<Review> reviews = book.get().getReviews();
            for (Review review : reviews) {
                responses.add(ReviewResponse.builder()
                                .nickname(review.getUser().getNickname())
                                .rating(review.getRating())
                                .review(review.getReview())
                                .build());
            }
        }
        return responses;
    }
}
