package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    List<Book> findAllByTitleOrAuthorContaining(String keyword);

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId")
    List<Book> findAllByGenreId(Long genreId);

    @Query("SELECT b FROM Book b WHERE b.language.id = :languageId")
    List<Book> findAllByLanguageId(Long languageId);

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId AND b.language.id = :languageId")
    List<Book> findAllByGenreIdAndLanguageId(Long genreId, Long languageId);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    Page<Book> findByTitleOrAuthorContaining(Pageable pageable, String keyword);

    Page<Book> findByOwner(Pageable pageable, User owner);
}
