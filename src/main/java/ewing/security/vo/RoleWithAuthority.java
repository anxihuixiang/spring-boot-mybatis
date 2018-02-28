package ewing.security.vo;

import ewing.query.entity.Authority;
import ewing.query.entity.Role;

import java.util.List;

public class RoleWithAuthority extends Role {

    List<Authority> authorities;

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
