package com.artsolo.bookswap.specifications;

import com.artsolo.bookswap.attributes.genre.Genre;
import com.artsolo.bookswap.book.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookSpecification {
    public static Specification<Book> hasGenres(List<Long> genreIds) {
        return (book, cq, cb) -> {
            if (genreIds == null || genreIds.isEmpty()) {
                return cb.isTrue(cb.literal(true));
            } else {
                Join<Book, Genre> genreJoin = book.join("genres", JoinType.INNER);
                return genreJoin.get("id").in(genreIds);
            }
        };
    }

    public static Specification<Book> hasLanguage(Long languageId) {
        return (book, cq, cb) -> {
            if (languageId == null) {
                return cb.isTrue(cb.literal(true));
            } else {
                return cb.equal(book.get("language").get("id"), languageId);
            }
        };
    }

    public static Specification<Book> hasQuality(Long qualityId) {
        return (book, cq, cb) -> {
            if (qualityId == null) {
                return cb.isTrue(cb.literal(true));
            } else {
                return cb.equal(book.get("quality").get("id"), qualityId);
            }
        };
    }

    public static Specification<Book> hasStatus(Long statusId) {
        return (book, cq, cb) -> {
            if (statusId == null) {
                return cb.isTrue(cb.literal(true));
            } else {
                return cb.equal(book.get("status").get("id"), statusId);
            }
        };
    }
}
