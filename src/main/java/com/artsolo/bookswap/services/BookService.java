package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.AddBookRequest;
import com.artsolo.bookswap.controllers.book.GetBookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    private final UserService userService;

    public BookService(BookRepository bookRepository, GenreRepository genreRepository,
                       QualityRepository qualityRepository, StatusRepository statusRepository,
                       LanguageRepository languageRepository, LibraryService libraryService,
                       LibraryRepository libraryRepository, WishlistRepository wishlistRepository,
                       ReviewRepository reviewRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.qualityRepository = qualityRepository;
        this.statusRepository = statusRepository;
        this.languageRepository = languageRepository;
        this.libraryService = libraryService;
        this.libraryRepository = libraryRepository;
        this.wishlistRepository = wishlistRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
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

    public boolean deleteBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book bot found"));
        CompositeKey compositeKey = new CompositeKey(book.getLibrary().getUser().getId(), book.getId());

        libraryRepository.deleteById(compositeKey);
        wishlistRepository.deleteById(compositeKey);
        reviewRepository.deleteById(compositeKey);

        libraryRepository.deleteById(new CompositeKey(book.getId(), book.getLibrary().getUser().getId()));
        bookRepository.deleteById(book.getId());
        return !bookRepository.existsById(id);
    }

    public GetBookResponse getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(value -> GetBookResponse.builder()
                .id(value.getId())
                .title(value.getTitle())
                .author(value.getAuthor())
                .genres(value.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                .quality(value.getQuality().getQuality())
                .status(value.getStatus().getStatus())
                .language(value.getLanguage().getLanguage())
                .photo(value.getPhoto())
                .build())
                .orElse(null);
    }

    public byte[] getBookPhoto(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(Book::getPhoto).orElse(null);
    }

    public boolean bookRequestIsValid(AddBookRequest request) {
        return (request.getTitle() != null && request.getAuthor() != null
                && request.getGenreIds() != null && request.getQualityId() != null
                && request.getStatusId() != null && request.getLanguageId() != null
                && request.getPhoto() != null);
    }
}
