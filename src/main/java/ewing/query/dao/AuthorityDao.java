package ewing.query.dao;

import ewing.query.mapper.AuthorityMapper;
import ewing.security.vo.AuthorityNode;

import java.util.List;

public interface AuthorityDao extends AuthorityMapper {

    List<AuthorityNode> getUserAuthorities(Long userId);

    List<AuthorityNode> getAuthorityNodes();

}