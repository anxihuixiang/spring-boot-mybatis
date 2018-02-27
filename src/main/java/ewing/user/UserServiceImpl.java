package ewing.user;

import ewing.application.AppAsserts;
import ewing.application.query.Page;
import ewing.entity.Role;
import ewing.entity.User;
import ewing.entity.UserRole;
import ewing.mapper.UserMapper;
import ewing.mapper.UserRoleMapper;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Long addUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.hasText(userWithRole.getUsername(), "用户名不能为空！");
        AppAsserts.hasText(userWithRole.getNickname(), "昵称不能为空！");
        AppAsserts.hasText(userWithRole.getPassword(), "密码不能为空！");
        AppAsserts.hasText(userWithRole.getGender(), "性别不能为空！");
        /*AppAsserts.yes(userMapper.countWhere(
                qUser.username.eq(userWithRole.getUsername())) < 1,
                "用户名已被使用！");*/

        userWithRole.setCreateTime(new Date());
        userMapper.insertSelective(userWithRole);
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
            /*userRoleMapper.insertBeans(userRoles.toArray());*/
        }
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.notNull(userWithRole.getUserId(), "用户ID不能为空！");

        // 更新用户的角色列表
        /*userRoleMapper.deleteWhere(qUserRole.userId.eq(userWithRole.getUserId()));*/
        addUserRoles(userWithRole);

        // 更新用户
        /*SQLUpdateClause update = userMapper.updaterByKey(userWithRole.getUserId());
        if (StringUtils.hasText(userWithRole.getNickname())) {
            update.set(qUser.nickname, userWithRole.getNickname());
        }
        if (StringUtils.hasText(userWithRole.getPassword())) {
            update.set(qUser.password, userWithRole.getPassword());
        }
        if (StringUtils.hasText(userWithRole.getGender())) {
            update.set(qUser.gender, userWithRole.getGender());
        }
        if (userWithRole.getBirthday() != null) {
            update.set(qUser.birthday, userWithRole.getBirthday());
        }
        return update.execute();*/
        return 0L;
    }

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        /*BooleanExpression expression = Expressions.TRUE;
        // 用户名
        expression = expression.and(StringUtils.hasText(findUserParam.getUsername())
                ? qUser.username.contains(findUserParam.getUsername()) : null);
        // 昵称
        expression = expression.and(StringUtils.hasText(findUserParam.getNickname())
                ? qUser.nickname.contains(findUserParam.getNickname()) : null);
        return userMapper.findUserWithRole(findUserParam, expression);*/
        return null;
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userMapper.deleteByPrimaryKey(userId);
    }

}
