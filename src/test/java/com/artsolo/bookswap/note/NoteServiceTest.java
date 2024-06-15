package com.artsolo.bookswap.note;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.note.dto.NoteResponse;
import com.artsolo.bookswap.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void notedSuccessfully() {
        User firstUser = User.builder().country("United Kingdom").city("Manchester").build();
        Book book = new Book();

        noteService.note(firstUser,book);
        ArgumentCaptor<Note> noteArgumentCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteArgumentCaptor.capture());
        Note capturedNote = noteArgumentCaptor.getValue();

        assertThat(capturedNote.getBook()).isEqualTo(book);
        assertThat(capturedNote.getCountry()).isEqualTo(firstUser.getCountry());
        assertThat(capturedNote.getCity()).isEqualTo(firstUser.getCity());
        assertThat(capturedNote.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void getNoteResponseIsValid() {
        Note note = Note.builder()
                .id(3L)
                .book(new Book())
                .country("USA")
                .city("New York")
                .date(LocalDate.now())
                .build();

        NoteResponse response = noteService.getNoteResponse(note);

        assertThat(response.getId()).isEqualTo(note.getId());
        assertThat(response.getCountry()).isEqualTo(note.getCountry());
        assertThat(response.getCity()).isEqualTo(note.getCity());
        assertThat(response.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void allBookNotesFound() {
        Book book = new Book();
        book.setId(1L);

        Note note1 = new Note();
        note1.setId(1L);
        note1.setCountry("Country 1");
        note1.setCity("City 1");
        note1.setDate(LocalDate.now());

        Note note2 = new Note();
        note2.setId(2L);
        note2.setCountry("Country 2");
        note2.setCity("City 2");
        note2.setDate(LocalDate.now());

        List<Note> mockNotes = new ArrayList<>();
        mockNotes.add(note1);
        mockNotes.add(note2);

        when(noteRepository.findAllByBookId(book.getId())).thenReturn(mockNotes);

        List<NoteResponse> result = noteService.getNotesByBook(book);

        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).getCountry()).isEqualTo("Country 1");
        assertThat(result.get(0).getCity()).isEqualTo("City 1");

        assertThat(result.get(1).getCountry()).isEqualTo("Country 2");
        assertThat(result.get(1).getCity()).isEqualTo("City 2");
    }
}