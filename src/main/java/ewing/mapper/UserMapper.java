package ewing.mapper;

import ewing.entity.User;
import tk.mybatis.mapper.common.Mapper;

/**
 * 用户数据库访问接口。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
public interface UserMapper extends Mapper<User> {

    void deleteAll();

}
