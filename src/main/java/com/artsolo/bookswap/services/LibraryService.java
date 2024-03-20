package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Library;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.LibraryRepository;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LibraryService(LibraryRepository libraryRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addNewBookToUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        libraryRepository.save(library);
        return true;
    }

    public boolean addNewBookToUserLibrary(Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
        if (user != null && book != null) {
            Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
            libraryRepository.save(library);
            return true;
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
                return true;
            }
        }
        return false;
    }

}
