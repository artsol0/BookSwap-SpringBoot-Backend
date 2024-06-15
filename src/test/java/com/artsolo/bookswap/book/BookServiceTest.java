package com.artsolo.bookswap.book;

import com.artsolo.bookswap.attributes.genre.Genre;
import com.artsolo.bookswap.attributes.genre.GenreService;
import com.artsolo.bookswap.attributes.language.Language;
import com.artsolo.bookswap.attributes.language.LanguageService;
import com.artsolo.bookswap.attributes.quality.Quality;
import com.artsolo.bookswap.attributes.quality.QualityService;
import com.artsolo.bookswap.attributes.status.Status;
import com.artsolo.bookswap.attributes.status.StatusService;
import com.artsolo.bookswap.book.dto.AddBookRequest;
import com.artsolo.bookswap.book.dto.BookResponse;
import com.artsolo.bookswap.book.dto.UpdateBookRequest;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.exchange.ExchangeRepository;
import com.artsolo.bookswap.note.NoteService;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.wishlist.WishlistRepository;
import com.artsolo.bookswap.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private GenreService genreService;

    @Mock
    private QualityService qualityService;

    @Mock
    private StatusService statusService;

    @Mock
    private LanguageService languageService;

    @Mock
    private NoteService noteService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookService bookService;

    @Test
    void bookFoundById() {
        Long bookId = 4L;
        Book book = Book.builder().id(bookId).title("Book").author("Author").build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Book result = bookService.getBookById(bookId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookId);
        assertThat(result.getTitle()).isEqualTo("Book");
        assertThat(result.getAuthor()).isEqualTo("Author");
    }

    @Test
    void thrownExceptionWhileGettingBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void newBookWasAdded() throws IOException {
        User user = User.builder().id(5L).build();

        List<Genre> genres = Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel"));
        Quality quality = new Quality(3L, "Bad");
        Status status = new Status(2L, "Ready");
        Language language = new Language(2L, "German");
        MockMultipartFile file = new MockMultipartFile("photo", "some file".getBytes());

        AddBookRequest addBookRequest = new AddBookRequest(
                "First Book",
                "First Author",
                "Long description",
                List.of(1L, 2L),
                3L,
                2L,
                2L,
                file
        );

        Book book = Book.builder()
                .owner(user)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(genres)
                .quality(quality)
                .status(status)
                .language(language)
                .photo("some file".getBytes())
                .build();

        Book svdBook = Book.builder()
                .id(3L)
                .owner(user)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(genres)
                .quality(quality)
                .status(status)
                .language(language)
                .photo("some file".getBytes())
                .build();

        when(genreService.getGenreById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return genres.stream()
                    .filter(genre -> genre.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        });
        when(qualityService.getQualityById(3L)).thenReturn(quality);
        when(statusService.getStatusById(2L)).thenReturn(status);
        when(languageService.getLanguageById(2L)).thenReturn(language);
        when(bookRepository.save(book)).thenReturn(svdBook);

        BookResponse response = bookService.addNewBook(addBookRequest, user);
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        verify(noteService).note(eq(user), eq(svdBook));
        verify(userService).increaseUserPoints(eq(10), eq(user));

        assertThat(response.getId()).isEqualTo(svdBook.getId());
        assertThat(response.getOwnerId()).isEqualTo(user.getId());
        assertThat(response.getTitle()).isEqualTo(svdBook.getTitle());
        assertThat(response.getAuthor()).isEqualTo(svdBook.getAuthor());
        assertThat(response.getDescription()).isEqualTo(svdBook.getDescription());
        assertThat(response.getGenres()).isEqualTo(Arrays.asList("Horror", "Novel"));
        assertThat(response.getQuality()).isEqualTo("Bad");
        assertThat(response.getStatus()).isEqualTo("Ready");
        assertThat(response.getLanguage()).isEqualTo("German");
    }

    @Test
    void getBookFromAddRequestIsValid() {
        List<Genre> genres = Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel"));
        Quality quality = new Quality(3L, "Bad");
        Status status = new Status(2L, "Ready");
        Language language = new Language(2L, "German");
        MockMultipartFile file = new MockMultipartFile("photo", "some file".getBytes());

        AddBookRequest addBookRequest = new AddBookRequest(
                "First Book",
                "First Author",
                "Long description",
                List.of(1L, 2L),
                3L,
                2L,
                2L,
                file
        );

        when(genreService.getGenreById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return genres.stream()
                    .filter(genre -> genre.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        });
        when(qualityService.getQualityById(3L)).thenReturn(quality);
        when(statusService.getStatusById(2L)).thenReturn(status);
        when(languageService.getLanguageById(2L)).thenReturn(language);

        Optional<Book> result = bookService.getBookFromAddRequest(addBookRequest);
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getTitle()).isEqualTo(addBookRequest.getTitle());
        assertThat(result.get().getAuthor()).isEqualTo(addBookRequest.getAuthor());
        assertThat(result.get().getDescription()).isEqualTo(addBookRequest.getDescription());
        assertThat(result.get().getGenres()).isEqualTo(genres);
        assertThat(result.get().getQuality()).isEqualTo(quality);
        assertThat(result.get().getStatus()).isEqualTo(status);
        assertThat(result.get().getLanguage()).isEqualTo(language);
        assertThat(result.get().getPhoto()).isEqualTo("some file".getBytes());
    }

    @Test
    void ownerWasChanged() {
        User user1 = User.builder().id(5L).build();
        User user2 = User.builder().id(6L).build();

        Book book1 = Book.builder().id(3L).owner(user1).build();
        Book swdBook = Book.builder().id(3L).owner(user2).build();

        when(bookRepository.save(book1)).thenReturn(swdBook);
        boolean result = bookService.changeBookOwner(book1, user2);
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        verify(noteService).note(eq(user2), eq(bookArgumentCaptor.getValue()));
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(capturedBook.getOwner().getId()).isEqualTo(user2.getId());
    }

    @Test
    void bookWasUpdated() {
        User user = User.builder().id(5L).build();

        List<Genre> genres = Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel"));
        Quality quality = new Quality(3L, "Bad");
        Status status = new Status(2L, "Ready");
        Language language = new Language(2L, "German");

        UpdateBookRequest request = new UpdateBookRequest(
                "Updated Book",
                "Updated Author",
                "Updated Long description",
                Arrays.asList(1L, 2L),
                3L,
                2L,
                2L
        );

        Book book = Book.builder()
                .id(3L)
                .owner(user)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(List.of(new Genre(1L, "Horror")))
                .quality(new Quality(2L, "Nice"))
                .status(new Status(1L, "Not ready"))
                .language(new Language(1L, "English"))
                .photo("some file".getBytes())
                .build();

        Book svdBook = Book.builder()
                .id(3L)
                .owner(user)
                .title("Updated Book")
                .author("Updated Author")
                .description("Updated Long description")
                .genres(genres)
                .quality(quality)
                .status(status)
                .language(language)
                .photo("some file".getBytes())
                .build();

        when(genreService.getGenreById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return genres.stream()
                    .filter(genre -> genre.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        });
        when(qualityService.getQualityById(3L)).thenReturn(quality);
        when(statusService.getStatusById(2L)).thenReturn(status);
        when(languageService.getLanguageById(2L)).thenReturn(language);
        when(bookRepository.save(book)).thenReturn(svdBook);

        bookService.updateBook(book, request);
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook.getId()).isEqualTo(svdBook.getId());
        assertThat(capturedBook.getOwner().getId()).isEqualTo(user.getId());
        assertThat(capturedBook.getTitle()).isEqualTo(svdBook.getTitle());
        assertThat(capturedBook.getAuthor()).isEqualTo(svdBook.getAuthor());
        assertThat(capturedBook.getDescription()).isEqualTo(svdBook.getDescription());
        assertThat(capturedBook.getGenres()).isEqualTo(genres);
        assertThat(capturedBook.getQuality()).isEqualTo(quality);
        assertThat(capturedBook.getStatus()).isEqualTo(status);
        assertThat(capturedBook.getLanguage()).isEqualTo(language);
    }

    @Test
    void getBookResponseIsValid() {
        User user1 = User.builder().id(5L).build();
        Book book1 = Book.builder()
                .id(3L)
                .owner(user1)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel")))
                .quality(new Quality(3L, "Bad"))
                .status(new Status(2L, "Ready"))
                .language(new Language(2L, "German"))
                .photo("some file".getBytes())
                .build();

        BookResponse response = bookService.getBookResponse(book1);

        assertThat(response.getId()).isEqualTo(book1.getId());
        assertThat(response.getOwnerId()).isEqualTo(user1.getId());
        assertThat(response.getTitle()).isEqualTo(book1.getTitle());
        assertThat(response.getAuthor()).isEqualTo(book1.getAuthor());
        assertThat(response.getDescription()).isEqualTo(response.getDescription());
        assertThat(response.getGenres()).isEqualTo(List.of("Horror", "Novel"));
        assertThat(response.getQuality()).isEqualTo("Bad");
        assertThat(response.getStatus()).isEqualTo("Ready");
        assertThat(response.getLanguage()).isEqualTo("German");
        assertThat(response.getPhoto()).isEqualTo(book1.getPhoto());
    }

    @Test
    void userIsBookOwner() {
        User user = User.builder().id(5L).build();
        Book book = Book.builder().id(3L).owner(user).build();
        boolean isUserBookOwner = bookService.userIsBookOwner(user, book);
        assertThat(isUserBookOwner).isTrue();
    }

    @Test
    void userIsNotBookOwner() {
        User user1 = User.builder().id(5L).build();
        User user2 = User.builder().id(6L).build();
        Book book = Book.builder().id(3L).owner(user1).build();
        boolean isUserBookOwner = bookService.userIsBookOwner(user2, book);
        assertThat(isUserBookOwner).isFalse();
    }

    @Test
    void booksFoundByOwner() {
        User user1 = User.builder().id(5L).build();

        Book book1 = Book.builder()
                .id(3L)
                .owner(user1)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel")))
                .quality(new Quality(3L, "Bad"))
                .status(new Status(2L, "Ready"))
                .language(new Language(2L, "German"))
                .photo("some file".getBytes())
                .build();

        Book book2 = Book.builder()
                .id(5L)
                .owner(user1)
                .title("Second Book")
                .author("Second Author")
                .description("Long description")
                .genres(List.of(new Genre(2L, "Novel")))
                .quality(new Quality(2L, "Nice"))
                .status(new Status(2L, "Ready"))
                .language(new Language(1L, "English"))
                .photo("some file2".getBytes())
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        Page<Book> bookPage = new PageImpl<>(books);

        when(bookRepository.findByOwner(PageRequest.of(0, 10), user1)).thenReturn(bookPage);
        Page<BookResponse> result = bookService.getBooksPagedByUser(PageRequest.of(0, 10), user1);

        assertThat(result.getTotalElements()).isEqualTo(bookPage.getTotalElements());
        result.forEach(bookResponse -> assertThat(bookResponse.getOwnerId()).isEqualTo(5L));
    }

    @Test
    void foundAllBooks() {
        User user1 = User.builder().id(5L).build();
        User user2 = User.builder().id(4L).build();

        Book book1 = Book.builder()
                .id(3L)
                .owner(user1)
                .title("First Book")
                .author("First Author")
                .description("Long description")
                .genres(Arrays.asList(new Genre(1L, "Horror"), new Genre(2L, "Novel")))
                .quality(new Quality(3L, "Bad"))
                .status(new Status(2L, "Ready"))
                .language(new Language(2L, "German"))
                .photo("some file".getBytes())
                .build();

        Book book2 = Book.builder()
                .id(5L)
                .owner(user1)
                .title("Second Book")
                .author("Second Author")
                .description("Long description")
                .genres(List.of(new Genre(2L, "Novel")))
                .quality(new Quality(2L, "Nice"))
                .status(new Status(2L, "Ready"))
                .language(new Language(1L, "English"))
                .photo("some file2".getBytes())
                .build();

        Book book3 = Book.builder()
                .id(1L)
                .owner(user2)
                .title("Third Book")
                .author("Third Author")
                .description("Long description")
                .genres(List.of(new Genre(1L, "Horror")))
                .quality(new Quality(2L, "Nice"))
                .status(new Status(2L, "Ready"))
                .language(new Language(1L, "German"))
                .photo("some file2".getBytes())
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        books.add(book3);
        Page<Book> bookPage = new PageImpl<>(books);

        when(bookRepository.findAll(PageRequest.of(0, 10))).thenReturn(bookPage);
        Page<BookResponse> result = bookService.getAllBooksPaged(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(bookPage.getTotalElements());
    }
}