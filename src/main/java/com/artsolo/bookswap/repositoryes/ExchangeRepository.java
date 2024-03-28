package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findAllByInitiatorId(Long initiatorId);
    List<Exchange> findAllByRecipientId(Long recipientId);
}
