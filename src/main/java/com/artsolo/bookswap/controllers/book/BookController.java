package com.artsolo.bookswap.controllers.book;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.enums.Role;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<?> addNewBook(@ModelAttribute AddBookRequest request, Principal currentUser) throws IOException {
        if (bookService.bookRequestIsValid(request)) {
            User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
            if (bookService.addNewBook(request, user)) {
                userService.increaseUserPoints(10, user);
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book was added successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Filed to add new book")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        if (bookService.userIsBookOwner(user, book) || user.getRole().equals(Role.ADMINISTRATOR)) {
            if (bookService.deleteBook(book)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book was deleted successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Book still exist")).build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the owner of the book to perform this action")).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBookById(@PathVariable Long id, @ModelAttribute UpdateBookRequest request, Principal currentUser) throws IOException {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        if (bookService.userIsBookOwner(user, book) || (user.getRole().equals(Role.ADMINISTRATOR) || user.getRole().equals(Role.MODERATOR))) {
            bookService.updateBook(book, request);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book was updated successfully").build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the owner of the book to perform this action")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBookResponse(bookService.getBookById(id));
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookResponse).build());
    }

    @GetMapping("/get/by/genre/{id}")
    public ResponseEntity<?> getBooksByGenreId(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByGenreId(id)).build());
    }

    @GetMapping("/get/by/language/{id}")
    public ResponseEntity<?> getBooksByLanguageId(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByLanguageId(id)).build());
    }

    @GetMapping("/get/by/keyword")
    public ResponseEntity<?> getBooksByTitleOrAuthor(@RequestParam("word") String keyword) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(bookService.getBooksByTitleOrAuthor(keyword)).build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String keyword)
    {
        Pageable pageable =  PageRequest.of(page, 10);
        if (keyword.isEmpty()) {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(bookService.getAllBooksPaged(pageable)).build());
        } else {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(bookService.getAllBooksPagedByKeyword(pageable, keyword)).build());
        }
    }

    @GetMapping("/get/by/genre/and/language")
    public ResponseEntity<?> getBooksByGenreAndLanguage(@RequestBody Map<String, String> request) {
        if (request.get("genreId") != null && request.get("languageId") != null) {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(bookService.getBooksByGenreIdAndLanguageId(
                            Long.parseLong(request.get("genreId")),
                            Long.parseLong(request.get("languageId"))
                    )).build());
            }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getBookPhoto(@RequestParam("id") Long id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bookService.getBookPhoto(bookService.getBookById(id)));
    }

    @GetMapping("/get/{id}/additional-info")
    public ResponseEntity<?> getBookAdditionalInfo(@PathVariable("id") Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok()
                .body(SuccessResponse.builder().data(bookService.getBookAdditionalInfo(user, book)).build());
    }


}
