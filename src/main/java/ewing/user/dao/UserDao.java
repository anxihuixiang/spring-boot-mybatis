package ewing.user.dao;

import ewing.query.mapper.UserMapper;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;

import java.util.List;

public interface UserDao extends UserMapper {

    long countUser(FindUserParam findUserParam);

    List<UserWithRole> findUserWithRole(FindUserParam findUserParam);

}