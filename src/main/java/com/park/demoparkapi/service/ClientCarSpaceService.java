package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.ClientCarSpace;
import com.park.demoparkapi.exception.EntityNotFoundException;
import com.park.demoparkapi.repository.ClientCarSpaceRepository;
import com.park.demoparkapi.repository.projection.ClientCarSpaceProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientCarSpaceService {

    private final ClientCarSpaceRepository clientCarSpaceRepo;

    @Transactional
    public ClientCarSpace save(ClientCarSpace clientCarSpace) {
        return clientCarSpaceRepo.save(clientCarSpace);
    }

    @Transactional(readOnly = true)
    public ClientCarSpace findByReceipt(String receipt) {
        return clientCarSpaceRepo.findByReceiptAndEndDateIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Recibo %s não encontrado no sistema ou check-out já realizado.", receipt)
                )
        );
    }

    @Transactional(readOnly = true)
    public long getTotalTimesFullParking(String cpf) {
        return clientCarSpaceRepo.countByClientCpfAndEndDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientCarSpaceProjection> findAllByClientCpf(String cpf, Pageable pageable) {
        return clientCarSpaceRepo.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientCarSpaceProjection> findAllByUserId(Long id, Pageable pageable) {
        return clientCarSpaceRepo.findAllByClientUserId(id, pageable);
    }
}
