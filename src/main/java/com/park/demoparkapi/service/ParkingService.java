package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.CarSpace;
import com.park.demoparkapi.entity.Client;
import com.park.demoparkapi.entity.ClientCarSpace;
import com.park.demoparkapi.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientCarSpaceService clientCarSpaceService;
    private final ClientService clientService;
    private final CarSpaceService carSpaceService;

    @Transactional
    public ClientCarSpace checkIn(ClientCarSpace clientCarSpace) {
        /* Recuperar o cliente pelo CPF para obter o objeto completo de cliente */
        Client client = clientService.findByCpf(clientCarSpace.getClient().getCpf());
        clientCarSpace.setClient(client);

        /* Busca por uma vaga livre por um querie method que procura a primeira vaga livre */
        CarSpace carSpace = carSpaceService.getFreeCarSpace();
        carSpace.setStatus(CarSpace.StatusCarSpace.OCCUPIED);

        /* Insere o objeto vaga, a data de entrada e o recibo (usando o gerador de recibo personalizado que criamos) */
        clientCarSpace.setCarSpace(carSpace);
        clientCarSpace.setEntryDate(LocalDateTime.now());
        clientCarSpace.setReceipt(ParkingUtils.generateReceipt());

        System.out.println("Recibo: " + clientCarSpace.getReceipt());

        return clientCarSpaceService.save(clientCarSpace);
    }

    @Transactional
    public ClientCarSpace checkout(String receipt) {
        ClientCarSpace clientCarSpace = clientCarSpaceService.findByReceipt(receipt);
        LocalDateTime endDate = LocalDateTime.now();

        /* Calcular custo total do estacionamento */
        BigDecimal value = ParkingUtils.calculateCost(clientCarSpace.getEntryDate(), endDate);

        /* Calcular e inserir desconto */
        long totalOfTimes = clientCarSpaceService.getTotalTimesFullParking(clientCarSpace.getClient().getCpf());
        BigDecimal discount = ParkingUtils.calculateDiscount(value, totalOfTimes);
        clientCarSpace.setDiscount(discount);

        clientCarSpace.setEndDate(endDate);
        clientCarSpace.setValue(value);
        clientCarSpace.getCarSpace().setStatus(CarSpace.StatusCarSpace.FREE);

        return clientCarSpaceService.save(clientCarSpace);
    }
}
