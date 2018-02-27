package ewing.mapper;

import ewing.entity.UserRole;
import ewing.entity.UserRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper {
    long countByExample(UserRoleExample example);

    int deleteByExample(UserRoleExample example);

    int deleteByPrimaryKey(@Param("roleId") Long roleId, @Param("userId") Long userId);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    List<UserRole> selectByExample(UserRoleExample example);

    UserRole selectByPrimaryKey(@Param("roleId") Long roleId, @Param("userId") Long userId);

    int updateByExampleSelective(@Param("record") UserRole record, @Param("example") UserRoleExample example);

    int updateByExample(@Param("record") UserRole record, @Param("example") UserRoleExample example);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);
}