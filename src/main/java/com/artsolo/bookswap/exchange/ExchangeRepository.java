package com.artsolo.bookswap.exchange;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findAllByInitiatorId(Long initiatorId);
    List<Exchange> findAllByRecipientId(Long recipientId);
    List<Exchange> findAllByBookId(Long bookId);
}
