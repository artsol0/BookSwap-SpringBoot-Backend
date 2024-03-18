package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}
