package ewing.security.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import ewing.application.query.BaseBeanDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.entity.Authority;
import ewing.entity.Role;
import ewing.query.QRole;
import ewing.security.vo.RoleWithAuthority;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色数据访问实现。
 */
@Repository
public class RoleDaoImpl extends BaseBeanDao<QRole, Role> implements RoleDao {

    private QBean<RoleWithAuthority> qRoleWithAuthority = Projections
            .bean(RoleWithAuthority.class, qRole.all());

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate) {
        // 查询角色总数
        SQLQuery<Role> roleQuery = queryFactory.selectFrom(qRole)
                .where(predicate);
        long total = roleQuery.fetchCount();
        // 分页查询并附带权限
        roleQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = queryFactory.select(qRoleWithAuthority, qAuthority)
                .from(roleQuery.as(qRole))
                .leftJoin(qRoleAuthority).on(qRole.roleId.eq(qRoleAuthority.roleId))
                .leftJoin(qAuthority).on(qRoleAuthority.authorityId.eq(qAuthority.authorityId))
                .fetch();
        // 取出角色并去重
        Map<Long, RoleWithAuthority> roleWithAuthorityMap = new HashMap<>(pager.getLimit());
        for (Tuple row : rows) {
            RoleWithAuthority roleWithAuthority = row.get(qRoleWithAuthority);
            if (roleWithAuthority != null) {
                RoleWithAuthority exists = roleWithAuthorityMap.putIfAbsent(
                        roleWithAuthority.getRoleId(), roleWithAuthority);
                roleWithAuthority = exists == null ? roleWithAuthority : exists;
                // 添加权限到角色中
                Authority authority = row.get(qAuthority);
                if (authority != null && authority.getAuthorityId() != null) {
                    if (roleWithAuthority.getAuthorities() == null) {
                        roleWithAuthority.setAuthorities(new ArrayList<>());
                    }
                    roleWithAuthority.getAuthorities().add(authority);
                }
            }
        }
        return new Page<>(total, new ArrayList<>(roleWithAuthorityMap.values()));
    }

    @Override
    public List<Role> getRolesByUser(Long userId) {
        // 用户->角色
        return queryFactory.selectDistinct(qRole)
                .from(qRole)
                .join(qUserRole)
                .on(qRole.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

}
