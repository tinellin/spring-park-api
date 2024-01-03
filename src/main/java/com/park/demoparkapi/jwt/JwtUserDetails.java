package com.park.demoparkapi.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
    private final com.park.demoparkapi.entity.User user;

    public JwtUserDetails(com.park.demoparkapi.entity.User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getRole() {
        return this.user.getRole().name();
    }
}
