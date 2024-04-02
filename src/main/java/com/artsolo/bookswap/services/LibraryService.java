package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.BookResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.LibraryRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final NoteService noteService;

    public LibraryService(LibraryRepository libraryRepository, NoteService noteService) {
        this.libraryRepository = libraryRepository;
        this.noteService = noteService;
    }

    public Library getLibraryById(CompositeKey compositeKey) {
        return libraryRepository.findById(compositeKey).orElseThrow(() ->
                new NoDataFoundException("Language", compositeKey.getUser_id(), compositeKey.getBook_id()));
    }

    public boolean addNewBookToUserLibrary(User user, Book book) {
        Library library = new Library(new CompositeKey(user.getId(), book.getId()), user, book);
        library = libraryRepository.save(library);
        noteService.note(user, book);
        return libraryRepository.existsById(library.getLibraryId());
    }

    public boolean removeBookFromUserLibrary(Library library) {
        libraryRepository.deleteById(library.getLibraryId());
        return !libraryRepository.existsById(library.getLibraryId());
    }

    public List<BookResponse> getAllLibraryBooks(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<Library> libraries = libraryRepository.findAllByUserId(user.getId());
        List<BookResponse> getBookResponses = new ArrayList<>();
        for (Library library : libraries) {
            getBookResponses.add(BookResponse.builder()
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
