package ewing.user;

import ewing.common.pagination.PageData;
import ewing.common.pagination.PageParam;
import ewing.entity.User;

/**
 * 用户服务接口。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
public interface UserService {

    User addUser(User user);

    User getUser(String id);

    void updateUser(User user);

    PageData<User> findUsers(PageParam pageParam, String name, Integer gender);

    void deleteUser(String id);

    void clearUsers();
}
