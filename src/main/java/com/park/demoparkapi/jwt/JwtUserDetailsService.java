package com.park.demoparkapi.jwt;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String username) {
        User.Role role = userService.getRoleById(username);
        return JwtUtils.createJwtToken(username, role.name().substring("ROLE_".length()));
    }
}
