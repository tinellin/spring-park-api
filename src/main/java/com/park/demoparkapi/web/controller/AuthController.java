package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.jwt.JwtToken;
import com.park.demoparkapi.jwt.JwtUserDetailsService;
import com.park.demoparkapi.web.dto.UserLoginDto;
import com.park.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Autenticação", description = "Recurso para proceder com a autenticação na API.")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authManager;

    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API.", responses = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso e retorno de um beaker token."),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDto dto, HttpServletRequest req) {
        log.info("Login authentication process with {}", dto.getUsername());

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            authManager.authenticate(authToken);

            JwtToken token = detailsService.getTokenAuthenticated(dto.getUsername());

            return ResponseEntity.ok(token);

        } catch (AuthenticationException ex) {
            log.warn("Bad credentials from username {}", dto.getUsername());
        }

        return ResponseEntity.badRequest().body(new ErrorMessage(req, HttpStatus.BAD_REQUEST, "Invalid credentials"));
    }
}
