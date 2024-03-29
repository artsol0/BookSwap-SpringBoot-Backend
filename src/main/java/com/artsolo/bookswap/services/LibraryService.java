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
    private final NoteService noteService;

    public LibraryService(LibraryRepository libraryRepository, UserRepository userRepository, BookRepository bookRepository, UserService userService, NoteService noteService) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.noteService = noteService;
    }

    public boolean addNewBookToUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        library = libraryRepository.save(library);
        noteService.note(user, book);
        return libraryRepository.existsById(library.getLibraryId());
    }

    public boolean addNewBookToUserLibrary(Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
        if (user != null && book != null) {
            Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
            noteService.note(user, book);
            library = libraryRepository.save(library);
            return libraryRepository.existsById(library.getLibraryId());
        }
        return false;
    }

    public boolean removeBookFromUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        libraryRepository.deleteById(library.getLibraryId());
        return !libraryRepository.existsById(library.getLibraryId());
    }

    public boolean removeBookFromUserLibrary(Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
        if (user != null && book != null) {
            Library library = libraryRepository.findById(new CompositeKey(user.getId(), book.getId())).orElse(null);
            if (library != null) {
                libraryRepository.delete(library);
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
                    .photo(library.getBook().getPhoto())
                    .build());
        }
        return getBookResponses;
    }

}
