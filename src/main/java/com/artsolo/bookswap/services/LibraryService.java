package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Library;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.LibraryRepository;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public boolean addNewBookToUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        libraryRepository.save(library);
        return true;
    }

}
