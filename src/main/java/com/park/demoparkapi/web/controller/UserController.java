package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.service.UserService;
import com.park.demoparkapi.web.dto.UserCreateDto;
import com.park.demoparkapi.web.dto.UserPasswordDto;
import com.park.demoparkapi.web.dto.UserResponseDto;
import com.park.demoparkapi.web.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    /* ResponseEntity: encapsula um objeto e demais metadados (cabeçalho, resposta, etc) */
    public ResponseEntity<UserResponseDto> create(@RequestBody() UserCreateDto createDto) {
        User response = userService.save(UserMapper.toUsuario(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable() Long id) {
        User user = userService.getById((id));
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable() Long id, @RequestBody() UserPasswordDto dto) {
        User updatedUser = userService.updatePassword(id, dto.getPassword(), dto.getNewPassword(), dto.getConfirmNewPassword());
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}

