package com.park.demoparkapi.web.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserPasswordDto {

    private String password;
    private String newPassword;
    private String confirmNewPassword;

}
