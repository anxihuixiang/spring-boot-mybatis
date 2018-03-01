package ewing.user;

import ewing.application.AppAsserts;
import ewing.application.query.Paging;
import ewing.application.query.QueryUtils;
import ewing.user.dao.UserDao;
import ewing.user.dao.UserRoleDao;
import ewing.query.entity.*;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public Long addUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.hasText(userWithRole.getUsername(), "用户名不能为空！");
        AppAsserts.hasText(userWithRole.getNickname(), "昵称不能为空！");
        AppAsserts.hasText(userWithRole.getPassword(), "密码不能为空！");
        AppAsserts.hasText(userWithRole.getGender(), "性别不能为空！");

        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userWithRole.getUsername());
        AppAsserts.yes(userDao.countByExample(userExample) < 1,
                "用户名已被使用！");

        userWithRole.setCreateTime(new Date());
        userDao.insertSelective(userWithRole);
        addUserRoles(userWithRole);
        return userWithRole.getUserId();
    }

    private void addUserRoles(UserWithRole userWithRole) {
        List<Role> roles = userWithRole.getRoles();
        if (roles != null && !roles.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>(roles.size());
            for (Role role : roles) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userWithRole.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setCreateTime(new Date());
                userRoles.add(userRole);
            }
            userRoleDao.insertUserRoles(userRoles);
        }
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.selectByPrimaryKey(userId);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.notNull(userWithRole.getUserId(), "用户ID不能为空！");

        // 更新用户的角色列表
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userWithRole.getUserId());
        userRoleDao.deleteByExample(userRoleExample);
        addUserRoles(userWithRole);

        // 更新用户
        User userUpdate = new User();
        userUpdate.setUserId(userWithRole.getUserId());
        if (StringUtils.hasText(userWithRole.getNickname())) {
            userUpdate.setNickname(userWithRole.getNickname());
        }
        if (StringUtils.hasText(userWithRole.getPassword())) {
            userUpdate.setPassword(userWithRole.getPassword());
        }
        if (StringUtils.hasText(userWithRole.getGender())) {
            userUpdate.setGender(userWithRole.getGender());
        }
        if (userWithRole.getBirthday() != null) {
            userUpdate.setBirthday(userWithRole.getBirthday());
        }
        return userDao.updateByPrimaryKeySelective(userUpdate);
    }

    @Override
    public Paging<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        findUserParam.setUsername(QueryUtils.likeContains(findUserParam.getUsername()));
        findUserParam.setNickname(QueryUtils.likeContains(findUserParam.getNickname()));
        return new Paging<>(userDao.countUser(findUserParam),
                userDao.findUserWithRole(findUserParam));
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.deleteByPrimaryKey(userId);
    }

}
