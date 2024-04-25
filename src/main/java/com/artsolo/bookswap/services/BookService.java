package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.AddBookRequest;
import com.artsolo.bookswap.controllers.book.BookAdditionalInfo;
import com.artsolo.bookswap.controllers.book.BookResponse;
import com.artsolo.bookswap.controllers.book.UpdateBookRequest;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final GenreService genreService;
    private final QualityService qualityService;
    private final StatusService statusService;
    private final LanguageService languageService;
    private final LibraryRepository libraryRepository;
    private final LibraryService libraryService;

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Book", id));
    }

    public BookResponse getBookResponse(Book book) {
        return BookResponse.builder()
                        .id(book.getId())
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

    public boolean addNewBook(AddBookRequest addBookRequest, User user) throws IOException, NoDataFoundException {
        List<Genre> genres = addBookRequest.getGenreIds().stream().map(genreService::getGenreById).collect(Collectors.toList());
        Quality quality = qualityService.getQualityById(addBookRequest.getQualityId());
        Status status = statusService.getStatusById(addBookRequest.getStatusId());
        Language language = languageService.getLanguageById(addBookRequest.getLanguageId());
        byte[] photo = addBookRequest.getPhoto().getBytes();

        Book book = Book.builder()
                .title(addBookRequest.getTitle())
                .author(addBookRequest.getAuthor())
                .description(addBookRequest.getDescription())
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
        Library library = libraryRepository.findByBookId(book.getId());
        libraryRepository.delete(library);
        bookRepository.deleteById(book.getId());
        return !bookRepository.existsById(book.getId());
    }

    public void updateBook(Book book, UpdateBookRequest request) throws IOException, NoDataFoundException {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setGenres(request.getGenreIds().stream().map(genreService::getGenreById).collect(Collectors.toList()));
        book.setQuality(qualityService.getQualityById(request.getQualityId()));
        book.setStatus(statusService.getStatusById(request.getStatusId()));
        book.setLanguage(languageService.getLanguageById(request.getLanguageId()));
        book.setPhoto(request.getPhoto().getBytes());
        bookRepository.save(book);
    }

    public byte[] getBookPhoto(Book book) {return book.getPhoto();}

    public boolean bookRequestIsValid(AddBookRequest request) {
        return (request.getTitle() != null && request.getAuthor() != null
                && request.getDescription() != null && request.getGenreIds() != null
                && request.getQualityId() != null && request.getStatusId() != null
                && request.getLanguageId() != null && request.getPhoto() != null);
    }

    public BookAdditionalInfo getBookAdditionalInfo(User user, Book book) {
        return BookAdditionalInfo.builder()
                .isUserBookOwner(userIsBookOwner(user, book))
                .isBookInWishlist(isBookInWishlist(user, book))
                .isBookInExchange(isBookInExchange(user, book))
                .build();
    }

    public boolean userIsBookOwner(User user, Book book) {
        return user.getLibrary().stream().map(Library::getBook).anyMatch(libraryBook ->
                libraryBook.getId().equals(book.getId()));
    }

    public boolean isBookInWishlist(User user, Book book) {
        return user.getWishlist().stream().map(Wishlist::getBook).anyMatch(wishlistBook ->
                wishlistBook.getId().equals(book.getId()));
    }

    public boolean isBookInExchange(User user, Book book) {
        return user.getInitiations().stream().map(Exchange::getBook).anyMatch(exchangedBook ->
                exchangedBook.getId().equals(book.getId()));
    }
}
