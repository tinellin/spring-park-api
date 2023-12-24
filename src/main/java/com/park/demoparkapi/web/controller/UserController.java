package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.service.UserService;
import com.park.demoparkapi.web.dto.UserCreateDto;
import com.park.demoparkapi.web.dto.UserPasswordDto;
import com.park.demoparkapi.web.dto.UserResponseDto;
import com.park.demoparkapi.web.dto.mapper.UserMapper;
import com.park.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
@Tag(name = "Users", description = "goal: save, edit and get users")

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Resource to create a new user", responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "User already registered.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Resources not processed by invalid input data.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping()
    /* ResponseEntity: encapsula um objeto e demais metadados (cabeçalho, resposta, etc) */
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody() UserCreateDto createDto) {
        User response = userService.save(UserMapper.toUsuario(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(response));
    }

    @Operation(summary = "Get user by id", description = "Resource to get user by id", responses = {
            @ApiResponse(responseCode = "200", description = "Resource successfully retrieved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable() Long id) {
        User user = userService.getById((id));
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @Operation(summary = "Update user password", description = "Resource to update user password.", responses = {
            @ApiResponse(responseCode = "204", description = "Resource successfully updated.",
                    content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Resource not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "400", description = "Password does not match.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable() Long id, @Valid @RequestBody() UserPasswordDto dto) {
        User updatedUser = userService.updatePassword(id, dto.getPassword(), dto.getNewPassword(), dto.getConfirmNewPassword());
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    @Operation(summary = "Get all users", description = "Resource to get all users.", responses = {
            @ApiResponse(responseCode = "200", description = "Resource successfully retrieved.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}

