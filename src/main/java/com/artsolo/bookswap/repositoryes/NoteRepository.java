package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByBookId(Long bookId);
}
