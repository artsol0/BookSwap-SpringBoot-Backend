package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.AddBookRequest;
import com.artsolo.bookswap.controllers.book.BookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final QualityRepository qualityRepository;
    private final StatusRepository statusRepository;
    private final LanguageRepository languageRepository;
    private final LibraryService libraryService;
    private final LibraryRepository libraryRepository;
    private final WishlistRepository wishlistRepository;
    private final ReviewRepository reviewRepository;

    public Optional<Book> getBookById(Long id) {return bookRepository.findById(id);}

    public BookResponse getBookResponse(Book book) {
        return BookResponse.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .genres(book.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                        .quality(book.getQuality().getQuality())
                        .status(book.getStatus().getStatus())
                        .language(book.getLanguage().getLanguage())
                        .photo(book.getPhoto())
                        .build();
    }

    public List<BookResponse> getBooksByGenreId(Long id) {
        List<Book> books = bookRepository.findAllByGenreId(id);
        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books) {
            bookResponses.add(getBookResponse(book));
        }
        return bookResponses;
    }

    public List<BookResponse> getBooksByLanguageId(Long id) {
        List<Book> books = bookRepository.findAllByLanguageId(id);
        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books) {
            bookResponses.add(getBookResponse(book));
        }
        return bookResponses;
    }

    public List<BookResponse> getBooksByTitleOrAuthor(String keyword) {
        List<Book> books = bookRepository.findAllByTitleOrAuthorContaining(keyword);
        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books) {
            bookResponses.add(getBookResponse(book));
        }
        return bookResponses;
    }

    public List<BookResponse> getBooksByGenreIdAndLanguageId(Long genreId, Long languageId) {
        List<Book> books = bookRepository.findAllByGenreIdAndLanguageId(genreId, languageId);
        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : books) {
            bookResponses.add(getBookResponse(book));
        }
        return bookResponses;
    }

    public boolean addNewBook(AddBookRequest addBookRequest, Principal currentUser) throws IOException {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        List<Genre> genres = addBookRequest.getGenreIds().stream().map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new RuntimeException("Genre not found"))).toList();

        Quality quality = qualityRepository.findById(addBookRequest.getQualityId())
                .orElseThrow(() -> new RuntimeException("Quality not found"));

        Status status = statusRepository.findById(addBookRequest.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Language language = languageRepository.findById(addBookRequest.getLanguageId())
                .orElseThrow(() -> new RuntimeException("Language not found"));

        byte[] photo = addBookRequest.getPhoto().getBytes();

        Book book = Book.builder()
                .title(addBookRequest.getTitle())
                .author(addBookRequest.getAuthor())
                .genres(genres)
                .quality(quality)
                .status(status)
                .language(language)
                .photo(photo)
                .build();

        Book newBook = bookRepository.save(book);
        return libraryService.addNewBookToUserLibrary(user, newBook);
    }

    public boolean deleteBook(Book book) {
        CompositeKey compositeKey = new CompositeKey(book.getLibrary().getUser().getId(), book.getId());

        libraryRepository.deleteById(compositeKey);
        wishlistRepository.deleteById(compositeKey);
        reviewRepository.deleteById(compositeKey);

        libraryRepository.deleteById(new CompositeKey(book.getId(), book.getLibrary().getUser().getId()));
        bookRepository.deleteById(book.getId());
        return !bookRepository.existsById(book.getId());
    }

    public byte[] getBookPhoto(Book book) {return book.getPhoto();}

    public boolean bookRequestIsValid(AddBookRequest request) {
        return (request.getTitle() != null && request.getAuthor() != null
                && request.getGenreIds() != null && request.getQualityId() != null
                && request.getStatusId() != null && request.getLanguageId() != null
                && request.getPhoto() != null);
    }
}
