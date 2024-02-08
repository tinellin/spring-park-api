package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.CarSpace;
import com.park.demoparkapi.service.CarSpaceService;
import com.park.demoparkapi.web.dto.CarSpaceCreateDto;
import com.park.demoparkapi.web.dto.CarSpaceResponseDto;
import com.park.demoparkapi.web.dto.mapper.CarSpaceMapper;
import com.park.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Vagas", description = "Contém todas as operações relativas ao recurso de uma vaga.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/carspaces")
public class CarSpaceController {

    private final CarSpaceService carSpaceService;

    @Operation(summary = "Criar uma nova vaga", description = "Recurso para criar uma nova vaga." +
        "Requisição exige uso de um bearer token. Acesso restrito a role='ADMIN'",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso criado com sucesso.", headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")),            @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENT",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class)),
            @ApiResponse(
                    responseCode = "409", description = "Vaga já cadastrada",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class)
                    )),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                        content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class)
            )))
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid CarSpaceCreateDto dto) {
        CarSpace carSpace = CarSpaceMapper.toCarSpace(dto);
        carSpaceService.save(carSpace);

        /* Adicionar location no header com o path de requisição para a vaga criada */
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{code}")
                .buildAndExpand(carSpace.getCode())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Localizar uma vaga", description = "Recurso para localizar uma vaga pelo seu código." +
            "Requisição exige uso de um bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso criado com sucesso.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema =@Schema(implementation = CarSpaceResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Vaga não localizada",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENT",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public CarSpaceResponseDto getByCode(@PathVariable String code) {
        return CarSpaceMapper.toDto(carSpaceService.findByCode(code));
    }
}
