package com.park.demoparkapi.web.dto.mapper;

import com.park.demoparkapi.entity.Client;
import com.park.demoparkapi.web.dto.ClientResponseDto;
import com.park.demoparkapi.web.dto.CreateClientDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    public static Client toClient(CreateClientDto dto) {
        return new ModelMapper().map(dto, Client.class);
    }

    public static ClientResponseDto toDto(Client client) {
        return new ModelMapper().map(client, ClientResponseDto.class);
    }
}
