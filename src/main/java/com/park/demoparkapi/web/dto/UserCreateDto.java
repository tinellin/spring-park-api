package com.park.demoparkapi.web.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserCreateDto {

    @NotBlank
    @Email(regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", message = "E-mail format is invalid.")
    private String username;

    @NotBlank
    @Size(min = 6, max = 6)
    private String password;

}
