package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.ReviveRequest;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Revive;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.ReviveRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ReviveService {
    private final ReviveRepository reviveRepository;
    private final BookRepository bookRepository;

    public ReviveService(ReviveRepository reviveRepository, BookRepository bookRepository) {
        this.reviveRepository = reviveRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addBookRevive(ReviveRequest reviveRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Optional<Book> book = bookRepository.findById(reviveRequest.getBookId());
        if (book.isPresent()) {
            Revive revive = Revive.builder()
                    .book(book.get())
                    .user(user)
                    .rating(reviveRequest.getRating())
                    .revive(reviveRequest.getRevive())
                    .build();

            Revive newRevive = reviveRepository.save(revive);
            return reviveRepository.existsById(newRevive.getReviveId());
        }
        return false;
    }

    public boolean deleteReviveById(Long bookId, Long userId) {
        Optional<Revive> revive = reviveRepository.findById(new CompositeKey(userId, bookId));
        if (revive.isPresent()) {
            reviveRepository.deleteById(revive.get().getReviveId());
            return reviveRepository.existsById(revive.get().getReviveId());
        }
        return false;
    }
}
