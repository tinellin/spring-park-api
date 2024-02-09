package com.park.demoparkapi.repository;

import com.park.demoparkapi.entity.CarSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarSpaceRepository extends JpaRepository<CarSpace, Long> {
    Optional<CarSpace> findByCode(String code);

    Optional<CarSpace> findFirstByStatus(CarSpace.StatusCarSpace statusCarSpace);
}
