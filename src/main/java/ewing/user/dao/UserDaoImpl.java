package ewing.user.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.query.BaseBeanDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.entity.Role;
import ewing.entity.User;
import ewing.query.QUser;
import ewing.security.SecurityUser;
import ewing.user.vo.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问实现。
 */
@Repository
public class UserDaoImpl extends BaseBeanDao<QUser, User> implements UserDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    @Override
    public SecurityUser getSecurityUser(String username) {
        return queryFactory.select(
                Projections.bean(SecurityUser.class, qUser.all()))
                .from(qUser)
                .where(qUser.username.eq(username))
                .fetchOne();
    }

    private QBean<UserWithRole> qUserWithRole = Projections
            .bean(UserWithRole.class, qUser.all());

    @Override
    public Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate) {
        // 查询用户总数
        SQLQuery<User> userQuery = queryFactory.selectFrom(qUser)
                .where(predicate);
        long total = userQuery.fetchCount();
        // 查询分页并附带角色
        userQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = queryFactory.select(qUserWithRole, qRole)
                .from(userQuery.as(qUser))
                .leftJoin(qUserRole).on(qUser.userId.eq(qUserRole.userId))
                .leftJoin(qRole).on(qUserRole.roleId.eq(qRole.roleId))
                .fetch();
        // 取出用户并去重
        Map<Long, UserWithRole> userWithRoleMap = new HashMap<>(pager.getLimit());
        for (Tuple row : rows) {
            UserWithRole userWithRole = row.get(qUserWithRole);
            if (userWithRole != null) {
                UserWithRole exists = userWithRoleMap.putIfAbsent(
                        userWithRole.getUserId(), userWithRole);
                userWithRole = exists == null ? userWithRole : exists;
                // 添加角色到用户中
                Role role = row.get(qRole);
                if (role != null && role.getRoleId() != null) {
                    if (userWithRole.getRoles() == null) {
                        userWithRole.setRoles(new ArrayList<>());
                    }
                    userWithRole.getRoles().add(role);
                }
            }
        }
        return new Page<>(total, new ArrayList<>(userWithRoleMap.values()));
    }
}
