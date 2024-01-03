package com.park.demoparkapi.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService detailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        final String token = req.getHeader(JwtUtils.JWT_AUTHORIZATION);

        if (token == null || !token.startsWith(JwtUtils.JWT_BEARER)) {
            log.info("Token is null, blank or not initialized with 'Bearer '.");
            filterChain.doFilter(req, res); /* Devolver para os objetos req e res para o contexto da requisição */
            return;
        }

        if (!JwtUtils.isTokenValid(token)) {
            log.warn("Token is invalid.");
            filterChain.doFilter(req, res);
            return;
        }

        String username = JwtUtils.getUsernameFromToken(token);

        toAuthentication(req, username);

        /*
        * Finalizou o processo de autenticação do usuário
        * Método solicitado é permitido o acesso
        * */
        filterChain.doFilter(req, res);
    }

    private void toAuthentication(HttpServletRequest req, String username) {
        UserDetails userDetails = detailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());

        /*
        *  Passando como parâmetro o objeto de requisição de forma que o Spring Security consiga unir
        *  as operações de segurança com as informações de requisição
        */
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
