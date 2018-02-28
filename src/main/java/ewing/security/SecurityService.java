package ewing.security;

import ewing.application.query.Paging;
import ewing.query.entity.Authority;
import ewing.query.entity.Role;
import ewing.security.vo.AuthorityNode;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;

import java.util.List;

/**
 * 安全服务接口。
 **/
public interface SecurityService {

    SecurityUser getSecurityUser(String username);

    List<Authority> getAllAuthority();

    void addAuthority(Authority authority);

    void updateAuthority(Authority authority);

    void deleteAuthority(Long authorityId);

    List<AuthorityNode> getAuthorityTree();

    List<AuthorityNode> getUserAuthorities(Long userId);

    List<Role> getAllRoles();

    Paging<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam);

    void addRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void deleteRole(Long roleId);

    boolean userHasPermission(Long userId, String action, String targetType, String targetId);
}
