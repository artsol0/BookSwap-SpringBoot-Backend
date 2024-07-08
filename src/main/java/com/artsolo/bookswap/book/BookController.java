package com.artsolo.bookswap.book;

import com.artsolo.bookswap.book.dto.AddBookRequest;
import com.artsolo.bookswap.book.dto.BookAdditionalInfo;
import com.artsolo.bookswap.book.dto.BookResponse;
import com.artsolo.bookswap.book.dto.UpdateBookRequest;
import com.artsolo.bookswap.attributes.FindByAttributesRequest;
import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.enums.Role;
import com.artsolo.bookswap.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse<BookResponse>> addNewBook(
            @ModelAttribute @Valid AddBookRequest request,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(bookService.addNewBook(request, user)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<?> deleteBookById(@PathVariable Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        if (bookService.userIsBookOwner(user, book) || user.getRole().equals(Role.ADMINISTRATOR)) {
            if (bookService.deleteBook(book)) {
                userService.decreaseUserPoints(10, user);
                return ResponseEntity.ok().body(MessageResponse.builder().message("Book was deleted successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Book still exist")).build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the owner of the book to perform this action")).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBookById(@PathVariable Long id, @ModelAttribute @Valid UpdateBookRequest request,
                                            Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        if (bookService.userIsBookOwner(user, book) || (user.getRole().equals(Role.ADMINISTRATOR) || user.getRole().equals(Role.MODERATOR))) {
            bookService.updateBook(book, request);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Book was updated successfully").build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the owner of the book to perform this action")).build());
    }

    @PutMapping("/change-photo/{id}")
    public ResponseEntity<?> changeBookPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo,
                                             Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        if (bookService.userIsBookOwner(user, book) || (user.getRole().equals(Role.ADMINISTRATOR) || user.getRole().equals(Role.MODERATOR))) {
            bookService.changeBookPhoto(book, photo);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Photo changed successfully").build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the owner of the book to perform this action")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SuccessResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBookResponse(bookService.getBookById(id));
        return ResponseEntity.ok().body(new SuccessResponse<>(bookResponse));
    }

    @GetMapping("/get/all")
    public ResponseEntity<SuccessResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String keyword)
    {
        Pageable pageable = PageRequest.of(page, 10);
        if (keyword.isEmpty()) {
            return ResponseEntity.ok().body(new SuccessResponse<>(bookService.getAllBooksPaged(pageable)));
        } else {
            return ResponseEntity.ok().body(new SuccessResponse<>(bookService.getAllBooksPagedByKeyword(pageable, keyword)));
        }
    }

    @GetMapping("/get/by/user")
    public ResponseEntity<SuccessResponse<Page<BookResponse>>> getCurrentUserBooks(
            @RequestParam(defaultValue = "0") int page, Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok().body(new SuccessResponse<>(bookService.getBooksPagedByUser(pageable, user)));
    }

    @PostMapping("/get/by/attributes")
    public ResponseEntity<SuccessResponse<Page<BookResponse>>> getBooksByAttributes(
            @RequestParam(defaultValue = "0") int page,
            @RequestBody @Valid FindByAttributesRequest request)
    {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok().body(new SuccessResponse<>(bookService.getAllBooksPagedByAttributes(pageable, request)));
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> getBookPhoto(@RequestParam("id") Long id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bookService.getBookPhoto(bookService.getBookById(id)));
    }

    @GetMapping("/get/{id}/additional-info")
    public ResponseEntity<SuccessResponse<BookAdditionalInfo>> getBookAdditionalInfo(@PathVariable("id") Long id,
                                                                                     Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok().body(new SuccessResponse<>(bookService.getBookAdditionalInfo(user, book)));
    }


}
