package com.artsolo.bookswap.note;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.note.dto.NoteResponse;
import com.artsolo.bookswap.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void note(User user, Book book) {
        Note newNote = Note.builder()
                .book(book)
                .country(user.getCountry())
                .city(user.getCity())
                .date(LocalDate.now())
                .build();
        noteRepository.save(newNote);
    }

    public List<NoteResponse> getNotesByBook(Book book) {
        List<Note> notes = noteRepository.findAllByBookId(book.getId());
        return notes.stream().map(this::getNoteResponse).collect(Collectors.toList());
    }

    public NoteResponse getNoteResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .country(note.getCountry())
                .city(note.getCity())
                .date(note.getDate())
                .build();
    }
}
