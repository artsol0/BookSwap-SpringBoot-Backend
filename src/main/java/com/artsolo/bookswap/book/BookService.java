package com.artsolo.bookswap.book;

import com.artsolo.bookswap.attributes.FindByAttributesRequest;
import com.artsolo.bookswap.attributes.genre.Genre;
import com.artsolo.bookswap.attributes.genre.GenreService;
import com.artsolo.bookswap.attributes.language.Language;
import com.artsolo.bookswap.attributes.language.LanguageService;
import com.artsolo.bookswap.attributes.quality.Quality;
import com.artsolo.bookswap.attributes.quality.QualityService;
import com.artsolo.bookswap.attributes.status.Status;
import com.artsolo.bookswap.attributes.status.StatusService;
import com.artsolo.bookswap.book.dto.AddBookRequest;
import com.artsolo.bookswap.book.dto.BookAdditionalInfo;
import com.artsolo.bookswap.book.dto.BookResponse;
import com.artsolo.bookswap.book.dto.UpdateBookRequest;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.exchange.Exchange;
import com.artsolo.bookswap.exchange.ExchangeRepository;
import com.artsolo.bookswap.compositekey.*;
import com.artsolo.bookswap.note.NoteService;
import com.artsolo.bookswap.specifications.BookSpecification;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.user.UserService;
import com.artsolo.bookswap.wishlist.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final WishlistRepository wishlistRepository;
    private final ExchangeRepository exchangeRepository;
    private final GenreService genreService;
    private final QualityService qualityService;
    private final StatusService statusService;
    private final LanguageService languageService;
    private final NoteService noteService;
    private final UserService userService;

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Book", id));
    }

    public Page<BookResponse> getBooksPagedByUser(Pageable pageable, User user) {
        Page<Book> bookPage = bookRepository.findByOwner(pageable, user);
        return bookPage.map(this::getBookResponse);
    }

    public Page<BookResponse> getAllBooksPaged(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(this::getBookResponse);
    }

    public Page<BookResponse> getAllBooksPagedByKeyword(Pageable pageable, String keyword) {
        Page<Book> bookPage = bookRepository.findByTitleOrAuthorContaining(pageable, keyword);
        return bookPage.map(this::getBookResponse);
    }

    public Page<BookResponse> getAllBooksPagedByAttributes(Pageable pageable, FindByAttributesRequest request) {
        Specification<Book> specification = where(BookSpecification.hasGenres(request.getGenreIds())
                .and(BookSpecification.hasQuality(request.getQualityId()))
                .and(BookSpecification.hasStatus(request.getStatusId()))
                .and(BookSpecification.hasLanguage(request.getLanguageId())));

        Page<Book> bookPage = bookRepository.findAll(specification, pageable);
        return bookPage.map(this::getBookResponse);
    }

    public BookResponse getBookResponse(Book book) {
        return BookResponse.builder()
                        .id(book.getId())
                        .ownerId(book.getOwner().getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .description(book.getDescription())
                        .genres(book.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                        .quality(book.getQuality().getQuality())
                        .status(book.getStatus().getStatus())
                        .language(book.getLanguage().getLanguage())
                        .photo(book.getPhoto())
                        .build();
    }

    @Transactional
    public BookResponse addNewBook(AddBookRequest addBookRequest, User user) {
        Book book = getBookFromAddRequest(addBookRequest).orElseThrow(() -> new IllegalArgumentException("Invalid add book request"));
        book.setOwner(user);
        Book newBook = bookRepository.save(book);
        newBook.setOwner(user);
        noteService.note(user, newBook);
        userService.increaseUserPoints(10, user);
        return getBookResponse(newBook);
    }

    public Optional<Book> getBookFromAddRequest(AddBookRequest addBookRequest) {
        try {
            List<Genre> genres = addBookRequest.getGenreIds().stream().map(genreService::getGenreById).collect(Collectors.toList());
            Quality quality = qualityService.getQualityById(addBookRequest.getQualityId());
            Status status = statusService.getStatusById(addBookRequest.getStatusId());
            Language language = languageService.getLanguageById(addBookRequest.getLanguageId());
            byte[] photo = addBookRequest.getPhoto().getBytes();

            return Optional.of(Book.builder()
                    .title(addBookRequest.getTitle())
                    .author(addBookRequest.getAuthor())
                    .description(addBookRequest.getDescription())
                    .genres(genres)
                    .quality(quality)
                    .status(status)
                    .language(language)
                    .photo(photo)
                    .build());

        } catch (IOException e) {
            log.error("Error occurred while reading book photo: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean deleteBook(Book book) {
        bookRepository.deleteById(book.getId());
        return !bookRepository.existsById(book.getId());
    }

    public void updateBook(Book book, UpdateBookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setGenres(request.getGenreIds().stream().map(genreService::getGenreById).collect(Collectors.toList()));
        book.setQuality(qualityService.getQualityById(request.getQualityId()));
        book.setStatus(statusService.getStatusById(request.getStatusId()));
        book.setLanguage(languageService.getLanguageById(request.getLanguageId()));
        bookRepository.save(book);
    }

    public void changeBookPhoto(Book book, MultipartFile photo) {
        try {
            byte[] newPhoto = photo.getBytes();
            book.setPhoto(newPhoto);
            bookRepository.save(book);
        } catch (IOException e) {
            log.error("Error occurred during changing user photo", e);
        }
    }

    public boolean changeBookOwner(Book book, User newOwner) {
        book.setOwner(newOwner);
        Book svdBook = bookRepository.save(book);
        noteService.note(newOwner, svdBook);
        return svdBook.getOwner().getId().equals(newOwner.getId());
    }

    public byte[] getBookPhoto(Book book) {return book.getPhoto();}

    public BookAdditionalInfo getBookAdditionalInfo(User user, Book book) {
        return BookAdditionalInfo.builder()
                .isUserBookOwner(userIsBookOwner(user, book))
                .isBookInWishlist(isBookInWishlist(user, book))
                .isBookInExchange(isBookInExchange(user, book))
                .build();
    }

    public boolean userIsBookOwner(User user, Book book) {
        return book.getOwner().getId().equals(user.getId());
    }

    public boolean isBookInWishlist(User user, Book book) {
        return wishlistRepository.existsById(new CompositeKey(user.getId(), book.getId()));
    }

    public boolean isBookInExchange(User user, Book book) {
        return exchangeRepository.findAllByInitiatorId(user.getId()).stream()
                .map(Exchange::getBook).anyMatch(exchangeBook -> exchangeBook.getId().equals(book.getId()));
    }
}
