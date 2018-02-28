package ewing.security;

import ewing.application.AppAsserts;
import ewing.application.query.DataUtils;
import ewing.application.query.Paging;
import ewing.query.dao.*;
import ewing.query.entity.*;
import ewing.security.vo.AuthorityNode;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 安全服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private RoleAuthorityDao roleAuthorityDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    public static final Pattern CODE_PATTERN = Pattern.compile("[a-zA-Z]|([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])");

    @Override
    public SecurityUser getSecurityUser(String username) {
        AppAsserts.hasText(username, "用户名不能为空！");
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> users = userDao.selectByExample(example);
        return DataUtils.getMaxOneAndCopy(users, new SecurityUser());
    }

    @Override
    public List<Authority> getAllAuthority() {
        return authorityDao.selectByExample(new AuthorityExample());
    }

    @Override
    public void addAuthority(Authority authority) {
        AppAsserts.notNull(authority, "权限信息不能为空！");
        AppAsserts.hasText(authority.getName(), "权限名称不能为空！");
        AppAsserts.matchPattern(authority.getCode(), CODE_PATTERN,
                "权限编码应由字母、数字和下划线组成，以字母开头、字母或数字结束！");
        AppAsserts.hasText(authority.getType(), "权限类型不能为空！");

        /*AppAsserts.yes(authorityDao.countWhere(qAuthority.name.eq(authority.getName())) < 1,
                "权限名称 " + authority.getName() + " 已存在！");
        AppAsserts.yes(authorityDao.countWhere(qAuthority.code.eq(authority.getCode())) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");*/

        // 内容不允许为空串
        if (!StringUtils.hasText(authority.getContent())) {
            authority.setContent(null);
        }
        authority.setCode(authority.getCode().toUpperCase());
        authority.setCreateTime(new Date());
        authorityDao.insertSelective(authority);
    }

    @Override
    public void updateAuthority(Authority authority) {
        AppAsserts.notNull(authority, "权限信息不能为空！");
        AppAsserts.notNull(authority.getAuthorityId(), "权限ID不能为空！");
        AppAsserts.hasText(authority.getName(), "权限名称不能为空！");
        AppAsserts.matchPattern(authority.getCode(), CODE_PATTERN,
                "权限编码应由字母、数字和下划线组成，以字母开头、字母或数字结束！");
        AppAsserts.hasText(authority.getType(), "权限类型不能为空！");

        /*AppAsserts.yes(authorityDao.countWhere(qAuthority.name.eq(authority.getName())
                        .and(qAuthority.authorityId.ne(authority.getAuthorityId()))) < 1,
                "权限名称 " + authority.getName() + " 已存在！");
        AppAsserts.yes(authorityDao.countWhere(qAuthority.code.eq(authority.getCode())
                        .and(qAuthority.authorityId.ne(authority.getAuthorityId()))) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");*/

        // 内容不允许为空串
        if (!StringUtils.hasText(authority.getContent())) {
            authority.setContent(null);
        }
        authority.setCode(authority.getCode().toUpperCase());
        authorityDao.updateByPrimaryKeySelective(authority);
    }

    @Override
    public void deleteAuthority(Long authorityId) {
        AppAsserts.notNull(authorityId, "权限ID不能为空！");

        /*AppAsserts.yes(authorityDao.countWhere(qAuthority.parentId.eq(authorityId)) < 1,
                "请先删除所有子权限！");
        AppAsserts.yes(roleAuthorityDao.countWhere(qRoleAuthority.authorityId.eq(authorityId)) < 1,
                "该权限已有角色正在使用！");*/

        authorityDao.deleteByPrimaryKey(authorityId);
    }

    @Override
    public List<AuthorityNode> getAuthorityTree() {
        return Collections.emptyList() /*TreeUtils.toTree(authorityDao.selector()
                .want(Projections.bean(AuthorityNode.class, qAuthority.all()))
                .fetch())*/;
    }

    @Override
    public List<AuthorityNode> getUserAuthorities(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return authorityDao.getUserAuthorities(userId);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.selectByExample(new RoleExample());
    }

    @Override
    public Paging<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam) {
        return null /*roleDao.findRoleWithAuthority(findRoleParam,
                StringUtils.hasText(findRoleParam.getSearch()) ?
                        qRole.name.contains(findRoleParam.getSearch()) : null)*/;
    }

    @Override
    public void addRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        AppAsserts.notNull(roleWithAuthority, "角色对象不能为空。");
        AppAsserts.notNull(roleWithAuthority.getName(), "角色名不能为空。");
        /*AppAsserts.yes(roleDao.countWhere(qRole.name.eq(roleWithAuthority.getName())) < 1,
                "角色名已被使用。");*/
        // 使用自定义VO新增角色
        roleWithAuthority.setCreateTime(new Date());
        roleDao.insertSelective(roleWithAuthority);

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    public void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        AppAsserts.notNull(roleWithAuthority, "角色对象不能为空。");
        AppAsserts.notNull(roleWithAuthority.getRoleId(), "角色ID不能为空。");
        AppAsserts.notNull(roleWithAuthority.getName(), "角色名不能为空。");
        // 名称存在并且不是自己
        /*AppAsserts.yes(roleDao.countWhere(qRole.name.eq(roleWithAuthority.getName())
                        .and(qRole.roleId.ne(roleWithAuthority.getRoleId()))) < 1,
                "角色名已被使用。");*/

        // 使用自定义VO更新角色
        roleDao.updateByPrimaryKeySelective(roleWithAuthority);

        // 清空角色权限关系
        /*roleAuthorityDao.deleteWhere(qRoleAuthority.roleId.eq(roleWithAuthority.getRoleId()));*/

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    public void deleteRole(Long roleId) {
        AppAsserts.notNull(roleId, "角色ID不能为空。");

        // 清空角色权限关系
        /*roleAuthorityDao.deleteWhere(qRoleAuthority.roleId.eq(roleId));*/

        roleDao.deleteByPrimaryKey(roleId);
    }

    private void addRoleAuthorities(RoleWithAuthority roleWithAuthority) {
        List<Authority> authorities = roleWithAuthority.getAuthorities();
        if (authorities != null) {
            List<RoleAuthority> roleAuthorities = new ArrayList<>(authorities.size());
            for (Authority authority : roleWithAuthority.getAuthorities()) {
                RoleAuthority roleAuthority = new RoleAuthority();
                AppAsserts.notNull(authority.getAuthorityId(), "权限ID不能为空。");
                roleAuthority.setAuthorityId(authority.getAuthorityId());
                roleAuthority.setRoleId(roleWithAuthority.getRoleId());
                roleAuthority.setCreateTime(new Date());
                roleAuthorities.add(roleAuthority);
            }
            /*roleAuthorityDao.insertBeans(roleAuthorities.toArray());*/
        }
    }

    @Override
    @Cacheable(cacheNames = "PermissionCache", key = "#userId.toString() + #action + #targetType + #targetId")
    public boolean userHasPermission(Long userId, String action, String targetType, String targetId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        AppAsserts.hasText(action, "权限操作不能为空！");
        AppAsserts.hasText(targetId, "资源ID不能为空！");
        return false /*permissionDao.selector()
                .where(qPermission.userId.eq(userId))
                .where(qPermission.action.eq(action))
                .where(qPermission.targetId.eq(targetId))
                .where(StringUtils.hasText(targetType) ? qPermission.targetType.eq(targetType) : null)
                .fetchCount() > 0*/;
    }

}
