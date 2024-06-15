package com.artsolo.bookswap.exchange;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "exchanges")
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private Boolean confirmed;
}
