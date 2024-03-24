package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.BookRequest;
import com.artsolo.bookswap.controllers.book.BookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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
    private final ReviveRepository reviveRepository;

    public BookService(BookRepository bookRepository, GenreRepository genreRepository,
                       QualityRepository qualityRepository, StatusRepository statusRepository,
                       LanguageRepository languageRepository, LibraryService libraryService,
                       LibraryRepository libraryRepository, WishlistRepository wishlistRepository,
                       ReviveRepository reviveRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.qualityRepository = qualityRepository;
        this.statusRepository = statusRepository;
        this.languageRepository = languageRepository;
        this.libraryService = libraryService;
        this.libraryRepository = libraryRepository;
        this.wishlistRepository = wishlistRepository;
        this.reviveRepository = reviveRepository;
    }

    public boolean addNewBook(BookRequest bookRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        List<Genre> genres = bookRequest.getGenreIds().stream().map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new RuntimeException("Genre not found"))).toList();

        Quality quality = qualityRepository.findById(bookRequest.getQualityId())
                .orElseThrow(() -> new RuntimeException("Quality not found"));

        Status status = statusRepository.findById(bookRequest.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Language language = languageRepository.findById(bookRequest.getLanguageId())
                .orElseThrow(() -> new RuntimeException("Language not found"));

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .genres(genres)
                .quality(quality)
                .status(status)
                .language(language)
                .build();

        Book newBook = bookRepository.save(book);
        return libraryService.addNewBookToUserLibrary(user, newBook);
    }

    public boolean deleteBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book bot found"));
        CompositeKey compositeKey = new CompositeKey(book.getLibrary().getUser().getId(), book.getId());

        libraryRepository.deleteById(compositeKey);
        wishlistRepository.deleteById(compositeKey);
        reviveRepository.deleteById(compositeKey);

        libraryRepository.deleteById(new CompositeKey(book.getId(), book.getLibrary().getUser().getId()));
        bookRepository.deleteById(book.getId());
        return !bookRepository.existsById(id);
    }

    public BookResponse getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        BookResponse bookResponse = new BookResponse();
        if (book.isPresent()) {
            bookResponse = BookResponse.builder()
                    .id(book.get().getId())
                    .title(book.get().getTitle())
                    .author(book.get().getAuthor())
                    .genres(book.get().getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                    .quality(book.get().getQuality().getQuality())
                    .status(book.get().getStatus().getStatus())
                    .language(book.get().getLanguage().getLanguage())
                    .build();
        }
        return bookResponse;
    }
}
