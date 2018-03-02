package ewing.security;

import ewing.application.AppAsserts;
import ewing.application.common.TreeUtils;
import ewing.application.query.Paging;
import ewing.application.query.QueryUtils;
import ewing.query.entity.*;
import ewing.security.dao.AuthorityDao;
import ewing.security.dao.PermissionDao;
import ewing.security.dao.RoleAuthorityDao;
import ewing.security.dao.RoleDao;
import ewing.security.vo.AuthorityNode;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;
import ewing.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
        return QueryUtils.getMaxOneAndCopy(users, new SecurityUser());
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

        AuthorityExample exampleName = new AuthorityExample();
        exampleName.createCriteria().andNameEqualTo(authority.getName());
        AppAsserts.yes(authorityDao.countByExample(exampleName) < 1,
                "权限名称 " + authority.getName() + " 已存在！");

        AuthorityExample exampleCode = new AuthorityExample();
        exampleCode.createCriteria().andCodeEqualTo(authority.getCode());
        AppAsserts.yes(authorityDao.countByExample(exampleCode) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");

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

        AuthorityExample exampleName = new AuthorityExample();
        exampleName.createCriteria().andNameEqualTo(authority.getName())
                .andAuthorityIdNotEqualTo(authority.getAuthorityId());
        AppAsserts.yes(authorityDao.countByExample(exampleName) < 1,
                "权限名称 " + authority.getName() + " 已存在！");

        AuthorityExample exampleCode = new AuthorityExample();
        exampleCode.createCriteria().andCodeEqualTo(authority.getCode())
                .andAuthorityIdNotEqualTo(authority.getAuthorityId());
        AppAsserts.yes(authorityDao.countByExample(exampleCode) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");

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

        AuthorityExample authorityExample = new AuthorityExample();
        authorityExample.createCriteria().andParentIdEqualTo(authorityId);
        AppAsserts.yes(authorityDao.countByExample(authorityExample) < 1,
                "请先删除所有子权限！");

        RoleAuthorityExample roleAuthorityExample = new RoleAuthorityExample();
        roleAuthorityExample.createCriteria().andAuthorityIdEqualTo(authorityId);
        AppAsserts.yes(roleAuthorityDao.countByExample(roleAuthorityExample) < 1,
                "该权限已有角色正在使用！");

        authorityDao.deleteByPrimaryKey(authorityId);
    }

    @Override
    public List<AuthorityNode> getAuthorityTree() {
        return TreeUtils.toTree(authorityDao.getAuthorityNodes(),
                ArrayList::new,
                AuthorityNode::getAuthorityId,
                AuthorityNode::getParentId,
                AuthorityNode::getChildren,
                AuthorityNode::setChildren);
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
        findRoleParam.setSearch(QueryUtils.likeContains(findRoleParam.getSearch()));
        return new Paging<>(roleDao.countRole(findRoleParam),
                roleDao.findRoleWithAuthority(findRoleParam));
    }

    @Override
    public void addRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        AppAsserts.notNull(roleWithAuthority, "角色对象不能为空。");
        AppAsserts.notNull(roleWithAuthority.getName(), "角色名不能为空。");

        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andNameEqualTo(roleWithAuthority.getName());
        AppAsserts.yes(roleDao.countByExample(roleExample) < 1,
                "角色名已被使用。");

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
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andNameEqualTo(roleWithAuthority.getName())
                .andRoleIdNotEqualTo(roleWithAuthority.getRoleId());
        AppAsserts.yes(roleDao.countByExample(roleExample) < 1,
                "角色名已被使用。");

        // 使用自定义VO更新角色
        roleDao.updateByPrimaryKeySelective(roleWithAuthority);

        // 清空角色权限关系
        RoleAuthorityExample roleAuthorityExample = new RoleAuthorityExample();
        roleAuthorityExample.createCriteria().andRoleIdEqualTo(roleWithAuthority.getRoleId());
        roleAuthorityDao.deleteByExample(roleAuthorityExample);

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    public void deleteRole(Long roleId) {
        AppAsserts.notNull(roleId, "角色ID不能为空。");

        // 清空角色权限关系
        RoleAuthorityExample roleAuthorityExample = new RoleAuthorityExample();
        roleAuthorityExample.createCriteria().andRoleIdEqualTo(roleId);
        roleAuthorityDao.deleteByExample(roleAuthorityExample);

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
            roleAuthorityDao.insertRoleAuthorities(roleAuthorities);
        }
    }

    @Override
    @Cacheable(cacheNames = "PermissionCache", key = "#userId.toString() + #action + #targetType + #targetId")
    public boolean userHasPermission(Long userId, String action, String targetType, String targetId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        AppAsserts.hasText(action, "权限操作不能为空！");
        AppAsserts.hasText(targetId, "资源ID不能为空！");
        PermissionExample permissionExample = new PermissionExample();
        PermissionExample.Criteria criteria = permissionExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andActionEqualTo(action)
                .andTargetIdEqualTo(targetId);
        if (StringUtils.hasText(targetType)) {
            criteria.andTargetTypeEqualTo(targetType);
        }
        return permissionDao.countByExample(permissionExample) > 0;
    }

}
