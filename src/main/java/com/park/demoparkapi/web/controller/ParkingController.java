package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.entity.ClientCarSpace;
import com.park.demoparkapi.jwt.JwtUserDetails;
import com.park.demoparkapi.repository.projection.ClientCarSpaceProjection;
import com.park.demoparkapi.service.ClientCarSpaceService;
import com.park.demoparkapi.service.ParkingService;
import com.park.demoparkapi.web.dto.PageableDto;
import com.park.demoparkapi.web.dto.ParkingCreateDto;
import com.park.demoparkapi.web.dto.ParkingResponseDto;
import com.park.demoparkapi.web.dto.mapper.ClientCarSpaceMapper;
import com.park.demoparkapi.web.dto.mapper.PageableMapper;
import com.park.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Estacionamentos", description = "Operações de registro de entrada e saída de um veículo do estacionamento.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {

    private final ParkingService parkingService;
    private final ClientCarSpaceService clientCarSpaceService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dar entrada de um veículo no estacionamento. " +
    "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
    security = @SecurityRequirement(name = "security"),
    responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ParkingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Causas possiveis: <br/>" +
                    "- CPF do cliente não cadastrado no sistema; <br/>" +
                    "- Nenhuma vaga livre foi localizada;",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@RequestBody @Valid ParkingCreateDto dto) {
        ClientCarSpace clientCarSpace = ClientCarSpaceMapper.toCarSpace(dto);
        parkingService.checkIn(clientCarSpace);
        ParkingResponseDto responseDto = ClientCarSpaceMapper.toDto(clientCarSpace);

        /* Adicionar location no header com o path de requisição para cliente-vaga criado */
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(responseDto.getReceipt())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation(summary = "Localizar um veículo estacionado", description = "Recurso para retornar um veículo estacionado " +
            "pelo nº do recibo. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Número do rebibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ParkingResponseDto getByReceipt(@PathVariable String receipt) {
        ClientCarSpace clientCarSpace = clientCarSpaceService.findByReceipt(receipt);
        return ClientCarSpaceMapper.toDto(clientCarSpace);
    }

    @Operation(summary = "Operação de check-out", description = "Recurso para dar saída de um veículo do estacionamento. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = { @Parameter(in = PATH, name = "receipt", description = "Número do rebibo gerado pelo check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso atualzado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo inexistente ou " +
                            "o veículo já passou pelo check-out.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ParkingResponseDto checkout(@PathVariable String receipt) {
        ClientCarSpace clientCarSpace = parkingService.checkout(receipt);
        return ClientCarSpaceMapper.toDto(clientCarSpace);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os " +
            "registros de estacionamentos do cliente por CPF. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "Nº do CPF referente ao cliente a ser consultado",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Representa a página retornada",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Campo padrão de ordenação 'dataEntrada,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public PageableDto getAllParkingByCpf(@PathVariable  String cpf,
                                                    @PageableDefault(size = 5, sort = "entryDate",
                                                            direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientCarSpaceProjection> projection = clientCarSpaceService.findAllByClientCpf(cpf, pageable);
        return PageableMapper.toDto(projection);
    }

    @GetMapping()
    @PreAuthorize("hasRole('CLIENT')")
    public PageableDto getAllClientParking(@AuthenticationPrincipal JwtUserDetails user,
            @PageableDefault(size = 5, sort = "entryDate",direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientCarSpaceProjection> projection = clientCarSpaceService.findAllByUserId(user.getId(), pageable);
        return PageableMapper.toDto(projection);
    }
}
