package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.Client;
import com.park.demoparkapi.exception.CpfUniqueViolationException;
import com.park.demoparkapi.exception.EntityNotFoundException;
import com.park.demoparkapi.repository.ClientRepository;
import com.park.demoparkapi.repository.projection.ClientProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepo;

    @Transactional
    public Client save(Client client) {
        try {
            return clientRepo.save(client);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF '%s' não pode ser cadastrado, já existe no sistema.", client.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente id=%s não encontrado no sistema", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<ClientProjection> findAll(Pageable pageable) {
        return clientRepo.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Client findUserByIdAuthenticated(Long id) {
        return clientRepo.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Client findByCpf(String cpf) {
        return clientRepo.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente com CPF '%s' não encontrado", cpf))
        );
    }
}
