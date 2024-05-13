package com.artsolo.bookswap.controllers.note;

import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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
    public ResponseEntity<SuccessResponse<List<NoteResponse>>> getAllNotesByBookId(@PathVariable Long id) {
        return ResponseEntity.ok().body(new SuccessResponse<>(noteService.getNotesByBook(bookService.getBookById(id))));
    }
}
