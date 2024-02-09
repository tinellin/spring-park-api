package com.park.demoparkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingCreateDto {

    @NotBlank
    @Size(min = 7, max = 7)
    @Pattern(regexp = "[A-Z]{3}[0-9][A-Z][0-9]{2}", message = "A placa do veículo deve seguir o padrão Mercosul: XXX0X0XX")
    private String licensePlate;

    @NotBlank
    private String carBrand;

    @NotBlank
    private String carModel;

    @NotBlank
    private String carColor;

    @NotBlank
    @Size(min = 11, max = 11)
    @CPF
    private String clientCpf;

}
