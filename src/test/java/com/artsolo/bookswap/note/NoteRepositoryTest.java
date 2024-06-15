package com.artsolo.bookswap.note;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.book.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private BookRepository bookRepository;

    private final Book firstBook = new Book();
    private final Book secondBook = new Book();

    @BeforeEach
    void setUp() {

        bookRepository.save(firstBook);
        bookRepository.save(secondBook);

        noteRepository.save(
                Note.builder()
                .book(firstBook)
                .build()
        );

        noteRepository.save(
                Note.builder()
                .book(secondBook)
                .build()
        );

        noteRepository.save(
                Note.builder()
                .book(firstBook)
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void foundFirstBookNotes() {
        List<Note> notes = noteRepository.findAllByBookId(firstBook.getId());
        assertThat(notes.size()).isEqualTo(2);
        notes.forEach(note -> assertThat(note.getBook().getId()).isEqualTo(firstBook.getId()));
    }

    @Test
    void foundSecondBookNotes() {
        List<Note> notes = noteRepository.findAllByBookId(secondBook.getId());
        assertThat(notes.size()).isEqualTo(1);
        notes.forEach(note -> assertThat(note.getBook().getId()).isEqualTo(secondBook.getId()));
    }
}