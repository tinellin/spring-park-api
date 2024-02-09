package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.CarSpace;
import com.park.demoparkapi.exception.CodeUniqueViolationException;
import com.park.demoparkapi.exception.EntityNotFoundException;
import com.park.demoparkapi.repository.CarSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarSpaceService {

    private final CarSpaceRepository carSpaceRepo;

    @Transactional
    public CarSpace save(CarSpace carSpace) {
        try {
            return carSpaceRepo.save(carSpace);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(
                    String.format("Vaga com código '%s' já cadastrada", carSpace.getCode())
            );
        }
    }

    @Transactional(readOnly = true)
    public CarSpace findByCode(String code) {
        return carSpaceRepo.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vaga com código %s não foi encontrada.", code))
        );
    }

    @Transactional(readOnly = true)
    public CarSpace getFreeCarSpace() {
        return carSpaceRepo.findFirstByStatus(CarSpace.StatusCarSpace.FREE).orElseThrow(
                () -> new EntityNotFoundException("Nenhuma vaga livre foi encontrada.")
        );
    }
}
