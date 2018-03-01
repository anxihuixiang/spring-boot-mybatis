package ewing.query.dao;

import ewing.query.mapper.RoleMapper;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;

import java.util.List;

public interface RoleDao extends RoleMapper {

    long countRole(FindRoleParam findRoleParam);

    List<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam);

}