package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Revive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviveRepository extends JpaRepository<Revive, CompositeKey> {
}
