package ewing.query.dao;

import ewing.query.entity.UserRole;
import ewing.query.mapper.UserRoleMapper;

import java.util.List;

public interface UserRoleDao extends UserRoleMapper {

    int insertUserRoles(List<UserRole> userRoles);

}