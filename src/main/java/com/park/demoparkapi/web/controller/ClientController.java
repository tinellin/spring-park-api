package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.Client;
import com.park.demoparkapi.jwt.JwtUserDetails;
import com.park.demoparkapi.repository.projection.ClientProjection;
import com.park.demoparkapi.service.ClientService;
import com.park.demoparkapi.service.UserService;
import com.park.demoparkapi.web.dto.ClientResponseDto;
import com.park.demoparkapi.web.dto.CreateClientDto;
import com.park.demoparkapi.web.dto.PageableDto;
import com.park.demoparkapi.web.dto.UserResponseDto;
import com.park.demoparkapi.web.dto.mapper.ClientMapper;
import com.park.demoparkapi.web.dto.mapper.PageableMapper;
import com.park.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user", description = "Resource to create a new user", responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "User already registered.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Resources not processed by invalid input data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ClientResponseDto create(@RequestBody @Valid CreateClientDto dto,
                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.getById(userDetails.getId()));
        clientService.save(client);

        return ClientMapper.toDto(client);
    }

    @Operation(summary = "Localizar um cliente", description = "Recurso para localizar um cliente pelo id.", responses = {
            @ApiResponse(responseCode = "200", description = "Resource successfully retrieved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponseDto getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ClientMapper.toDto(client);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public PageableDto getAll(Pageable pageable) {
        Page<ClientProjection> clients = clientService.findAll(pageable);
        return PageableMapper.toDto(clients);
    }

    @Operation(summary = "Recuperar dados do cliente autenticado",
        description = "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
        security = @SecurityRequirement(name = "security"),
        responses = {
                @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                        content = @Content(mediaType = " application/json;charset=UTF-8",
                                schema = @Schema(implementation = ClientResponseDto.class))
                ),
                @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                        content = @Content(mediaType = " application/json;charset=UTF-8",
                                schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/details")
    @PreAuthorize("hasRole('CLIENT')")
    public ClientResponseDto getDetails(@AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.findUserByIdAuthenticated(userDetails.getId());
        return ClientMapper.toDto(client);
    }
}
