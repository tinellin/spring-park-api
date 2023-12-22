package com.park.demoparkapi.web.dto.mapper;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.web.dto.UserCreateDto;
import com.park.demoparkapi.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

     public static User toUsuario(UserCreateDto createDto) {
         return new ModelMapper().map(createDto, User.class);
     }

    public static UserResponseDto toDto(User user) {
        String role = user.getRole().name().substring("ROLE_".length());
        PropertyMap<User, UserResponseDto> props = new PropertyMap<User, UserResponseDto>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);

        return mapper.map(user, UserResponseDto.class);
    }

    public static List<UserResponseDto> toListDto(List<User> users) {
         return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
    }
}
