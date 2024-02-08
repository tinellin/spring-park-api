package com.park.demoparkapi.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateClientDto {

    @NotNull
    @Size(min = 5, max = 100)
    private String name;

    @Size(min = 11, max = 11)
    @CPF
    private String cpf;
}
