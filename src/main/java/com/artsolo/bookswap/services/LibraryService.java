package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.GetBookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.LibraryRepository;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public LibraryService(LibraryRepository libraryRepository, UserRepository userRepository, BookRepository bookRepository, UserService userService) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    /*
    * Increasing the user's points if he added the book himself
    * */
    public boolean addNewBookToUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        library = libraryRepository.save(library);
        userService.increaseUserPoints(15, user);
        return libraryRepository.existsById(library.getLibraryId());
    }

    /*
     * Decreasing the user's points if the book was added by the system during the exchange
     * */
    public boolean addNewBookToUserLibrary(Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
        if (user != null && book != null) {
            Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
            library = libraryRepository.save(library);
            userService.decreaseUserPoints(15, user);
            return libraryRepository.existsById(library.getLibraryId());
        }
        return false;
    }

    public boolean removeBookFromUserLibrary(Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
        if (user != null && book != null) {
            Library library = libraryRepository.findById(new CompositeKey(user.getId(), book.getId())).orElse(null);
            if (library != null) {
                libraryRepository.delete(library);
                userService.increaseUserPoints(20, user);
                return !libraryRepository.existsById(library.getLibraryId());
            }
        }
        return false;
    }

    public List<GetBookResponse> getAllLibraryBooks(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<Library> libraries = libraryRepository.findByUserId(user.getId());
        List<GetBookResponse> getBookResponses = new ArrayList<>();
        for (Library library : libraries) {
            getBookResponses.add(GetBookResponse.builder()
                    .id(library.getBook().getId())
                    .title(library.getBook().getTitle())
                    .author(library.getBook().getAuthor())
                    .genres(library.getBook().getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                    .quality(library.getBook().getQuality().getQuality())
                    .status(library.getBook().getStatus().getStatus())
                    .language(library.getBook().getLanguage().getLanguage())
                    .build());
        }
        return getBookResponses;
    }

}
