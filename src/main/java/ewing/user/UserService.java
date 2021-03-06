package ewing.user;

import ewing.application.query.Paging;
import ewing.query.entity.User;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;

/**
 * 用户服务接口。
 **/
public interface UserService {

    User getUser(Long userId);

    Long addUserWithRole(UserWithRole userWithRole);

    long updateUserWithRole(UserWithRole userWithRole);

    Paging<UserWithRole> findUserWithRole(FindUserParam findUserParam);

    long deleteUser(Long userId);

}
