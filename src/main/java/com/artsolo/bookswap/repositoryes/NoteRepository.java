package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
