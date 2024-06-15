package com.artsolo.bookswap.user;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.exchange.Exchange;
import com.artsolo.bookswap.review.Review;
import com.artsolo.bookswap.token.Token;
import com.artsolo.bookswap.wishlist.Wishlist;
import com.artsolo.bookswap.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(unique = true, length = 35)
    private String nickname;
    private String email;
    private String password;
    private Integer points;
    @Lob
    @Column(columnDefinition="BLOB")
    private byte[] photo;
    private Boolean activity;
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    private String country;
    private String city;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Token> tokens;
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Book> books;
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Wishlist> wishlist;
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Review> reviews;
    @OneToMany(mappedBy = "initiator", orphanRemoval = true)
    private List<Exchange> initiations;
    @OneToMany(mappedBy = "recipient", orphanRemoval = true)
    private List<Exchange> recipients;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activity;
    }
}
