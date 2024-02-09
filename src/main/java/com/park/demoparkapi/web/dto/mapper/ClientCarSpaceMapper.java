package com.park.demoparkapi.web.dto.mapper;

import com.park.demoparkapi.entity.ClientCarSpace;
import com.park.demoparkapi.web.dto.ParkingCreateDto;
import com.park.demoparkapi.web.dto.ParkingResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientCarSpaceMapper {

    public static ClientCarSpace toCarSpace(ParkingCreateDto dto) {
        return new ModelMapper().map(dto, ClientCarSpace.class);
    }

    public static ParkingResponseDto toDto(ClientCarSpace clientCarSpace) {
        return new ModelMapper().map(clientCarSpace, ParkingResponseDto.class);
    }
}
