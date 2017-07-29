package ewing.user;

import ewing.application.AppException;
import ewing.common.GlobalIdWorker;
import ewing.common.pagination.PageData;
import ewing.common.pagination.PageParam;
import ewing.entity.User;
import ewing.mapper.UserMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 用户服务实现。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@Service
@Transactional
@CacheConfig(cacheNames = "UserCache")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User addUser(User user) {
        if (!StringUtils.hasText(user.getName()))
            throw new AppException("用户名不能为空！");
        User example = new User();
        example.setName(user.getName());
        if (userMapper.selectCount(example) > 0)
            throw new AppException("用户名已被使用！");
        if (!StringUtils.hasText(user.getPassword()))
            throw new AppException("密码不能为空！");
        user.setUserId(GlobalIdWorker.nextString());
        user.setCreateTime(new Date());
        userMapper.insert(user);
        return user;
    }

    @Override
    @Cacheable(unless = "#result==null")
    public User getUser(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @CacheEvict(key = "#user.userId")
    public void updateUser(User user) {
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public PageData<User> findUsers(PageParam pageParam, String name, Integer gender) {
        Example example = new Example(User.class);

        if (StringUtils.hasText(name))
            example.createCriteria().andLike("name", "%" + name + "%");

        if (gender != null)
            example.createCriteria().andEqualTo("gender", gender);

        List<User> users = userMapper.selectByExampleAndRowBounds(example,
                new RowBounds(pageParam.getOffset(), pageParam.getLimit()));

        if (pageParam.isCount()) {
            return new PageData<>(userMapper.selectCountByExample(example), users);
        } else {
            return new PageData<>(users);
        }
    }

    @Override
    @CacheEvict
    public void deleteUser(String id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void clearUsers() {
        userMapper.deleteAll();
    }

}
