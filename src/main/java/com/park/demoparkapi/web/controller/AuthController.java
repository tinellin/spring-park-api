package com.park.demoparkapi.web.controller;

import com.park.demoparkapi.jwt.JwtToken;
import com.park.demoparkapi.jwt.JwtUserDetailsService;
import com.park.demoparkapi.web.dto.UserLoginDto;
import com.park.demoparkapi.web.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authManager;

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
