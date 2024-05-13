package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.note.NoteResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.Note;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        List<NoteResponse> responses = new ArrayList<>();
        for (Note note : notes) {
            responses.add(NoteResponse.builder()
                    .id(note.getId())
                    .country(note.getCountry())
                    .city(note.getCity())
                    .date(note.getDate())
                    .build());
        }
        return responses;
    }
}
