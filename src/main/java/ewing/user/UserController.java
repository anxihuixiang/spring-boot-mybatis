package ewing.user;

import ewing.application.Result;
import ewing.common.pagination.PageData;
import ewing.common.pagination.PageParam;
import ewing.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@RestController
@RequestMapping("user")
@Api(description = "用户模块的方法")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("findUser")
    @ApiOperation("分页查找用户")
    public Result<PageData<User>> findUsers(PageParam pageParam, String name, Integer gender) {
        return new Result<>(userService.findUsers(pageParam, name, gender));
    }

    @PostMapping("addUser")
    @ApiOperation("添加用户")
    public Result<User> addUser(User user) {
        return new Result<>(userService.addUser(user));
    }

    @PostMapping("updateUser")
    @ApiOperation("更新用户")
    public Result updateUser(User user) {
        userService.updateUser(user);
        return new Result();
    }

    @GetMapping("getUser")
    @ApiOperation("根据ID获取用户")
    public Result<User> getUser(String id) {
        return new Result<>(userService.getUser(id));
    }

    @GetMapping("deleteUser")
    @ApiOperation("根据ID删除用户")
    public Result deleteUser(String id) {
        userService.deleteUser(id);
        return new Result();
    }

    @GetMapping("clearUsers")
    @ApiOperation("删除所有用户")
    public Result clearUsers() {
        userService.clearUsers();
        return new Result();
    }

}
