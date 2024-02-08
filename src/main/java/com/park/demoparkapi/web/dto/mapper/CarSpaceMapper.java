package com.park.demoparkapi.web.dto.mapper;

import com.park.demoparkapi.entity.CarSpace;
import com.park.demoparkapi.web.dto.CarSpaceCreateDto;
import com.park.demoparkapi.web.dto.CarSpaceResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CarSpaceMapper {

    public static CarSpace toCarSpace(CarSpaceCreateDto dto) {
        return new ModelMapper().map(dto, CarSpace.class);
    }

    public static CarSpaceResponseDto toDto(CarSpace carSpace) {
            return new ModelMapper().map(carSpace, CarSpaceResponseDto.class);
    }
}
