package ewing.mapper;

import ewing.entity.RoleAuthority;
import ewing.entity.RoleAuthorityExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleAuthorityMapper {
    long countByExample(RoleAuthorityExample example);

    int deleteByExample(RoleAuthorityExample example);

    int deleteByPrimaryKey(@Param("roleId") Long roleId, @Param("authorityId") Long authorityId);

    int insert(RoleAuthority record);

    int insertSelective(RoleAuthority record);

    List<RoleAuthority> selectByExample(RoleAuthorityExample example);

    RoleAuthority selectByPrimaryKey(@Param("roleId") Long roleId, @Param("authorityId") Long authorityId);

    int updateByExampleSelective(@Param("record") RoleAuthority record, @Param("example") RoleAuthorityExample example);

    int updateByExample(@Param("record") RoleAuthority record, @Param("example") RoleAuthorityExample example);

    int updateByPrimaryKeySelective(RoleAuthority record);

    int updateByPrimaryKey(RoleAuthority record);
}