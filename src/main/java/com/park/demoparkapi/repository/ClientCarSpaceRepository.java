package com.park.demoparkapi.repository;

import com.park.demoparkapi.entity.ClientCarSpace;
import com.park.demoparkapi.repository.projection.ClientCarSpaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientCarSpaceRepository extends JpaRepository<ClientCarSpace, Long> {
    Optional<ClientCarSpace> findByReceiptAndEndDateIsNull(String receipt);

    long countByClientCpfAndEndDateIsNotNull(String cpf);

    Page<ClientCarSpaceProjection> findAllByClientCpf(String cpf, Pageable pageable);

    Page<ClientCarSpaceProjection> findAllByClientUserId(Long id, Pageable pageable);
}
