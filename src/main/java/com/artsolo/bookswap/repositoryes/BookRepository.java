package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:parameter% OR b.author LIKE %:parameter%")
    List<Book> findAllByTitleOrAuthorContaining(String parameter);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds")
    List<Book> findAllByGenreIds(List<Long> genreIds);

    @Query("SELECT b FROM Book b WHERE b.language.id = :languageId")
    List<Book> findAllByLanguageId(String languageId);
}
