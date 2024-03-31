package com.artsolo.bookswap.controllers.note;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    private final NoteService noteService;
    private final BookService bookService;

    public NoteController(NoteService noteService, BookService bookService) {
        this.noteService = noteService;
        this.bookService = bookService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAllNotesByBookId(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok().body(SuccessResponse.builder()
                        .data(noteService.getNotesByBook(book.get())).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Book with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }
}
