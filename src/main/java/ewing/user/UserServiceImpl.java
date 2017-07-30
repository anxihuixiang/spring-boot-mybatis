package ewing.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import ewing.application.AppException;
import ewing.common.GlobalIdWorker;
import ewing.common.pagination.PageData;
import ewing.common.pagination.PageParam;
import ewing.entity.User;
import ewing.entity.UserExample;
import ewing.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

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
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(user.getName());
        if (userMapper.countByExample(example) > 0)
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
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name))
            criteria.andNameLike("%" + name + "%");

        if (gender != null)
            criteria.andGenderEqualTo(gender);

        Page<User> userPage = PageHelper.offsetPage(pageParam.getOffset(), pageParam.getLimit(), pageParam.isCount());

        userMapper.selectByExample(example);

        if (pageParam.isCount()) {
            return new PageData<>(userPage.getTotal(), userPage);
        } else {
            return new PageData<>(userPage);
        }
    }

    @Override
    @CacheEvict
    public void deleteUser(String id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void clearUsers() {
        userMapper.deleteByExample(new UserExample());
    }

}
