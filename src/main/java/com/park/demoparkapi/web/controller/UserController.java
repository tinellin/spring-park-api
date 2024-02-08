package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.service.UserService;
import com.park.demoparkapi.web.dto.ClientResponseDto;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Users", description = "goal: save, edit and get users")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente vinculado a um usuário cadastrado. " +
                    "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente CPF já possui cadastro no sistema",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping()
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody() UserCreateDto createDto) {
        User response = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(response));
    }

    @Operation(summary = "Localizar um cliente", description = "Recurso para localizar um cliente pelo ID. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR ( hasRole('CLIENT') AND #id == authentication.principal.id )") // comentar isso dps
    public ResponseEntity<UserResponseDto> getById(@PathVariable() Long id) {
        User user = userService.getById((id));
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @Operation(summary = "Update user password", description = "Resource to update user password.", responses = {
            @ApiResponse(responseCode = "204", description = "Resource successfully updated."),
            @ApiResponse(responseCode = "400", description = "Password does not match.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid fields.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT') AND ( #id == authentication.principal.id )")
    public ResponseEntity<Void> updatePassword(@PathVariable() Long id, @Valid @RequestBody() UserPasswordDto dto) {
        userService.updatePassword(id, dto.getPassword(), dto.getNewPassword(), dto.getConfirmNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users", description = "Resource to get all users.", responses = {
            @ApiResponse(responseCode = "200", description = "Resource successfully retrieved.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}

