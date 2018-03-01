package ewing.query.dao;

import ewing.query.entity.RoleAuthority;
import ewing.query.mapper.RoleAuthorityMapper;

import java.util.List;

public interface RoleAuthorityDao extends RoleAuthorityMapper {

    int insertRoleAuthorities(List<RoleAuthority> records);

}